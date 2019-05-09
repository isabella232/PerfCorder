package org.restcomm.perfcorder.analyzer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

public class GraphGenerator {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(PerfCorderAnalyzeApp.class.getName());

    public static String GRAPHS_LOC = "./graphs";

    public static String generateGraph(CSVColumnMeasTarget target, List<String[]> readAll, PerfCorderAnalysis analysis) throws IOException {
        TimeSeries tSeries = new TimeSeries(target.getLabel());
        Second current = new Second(new Date(analysis.getStartTimeStamp()));
        //assume first lines contains column names
        int stripWithHeader = 1;

        int previousDelta = 0;//used to calcualte delta for auxTSColums

        Second statsStartSecond = null;
        Second statsEndSecond = null;
        for (int i = stripWithHeader; i < readAll.size(); i++) {
            String[] readNext = readAll.get(i);
            int column = target.getColumn();
            if (column < readNext.length && column >= 0) {
                String nextCol = readNext[column];
                double nexValue = target.transformIntoDouble(nextCol);
                //increment using meas interval to produce correct time scale
                int measDeltaSeconds = analysis.getSettings().getMeasIntervalSeconds();
                //If target has auxiliary ts col, use it to calculate delta
                if (target.getAuxTimestampColumn() >= 0) {
                    measDeltaSeconds = Integer.parseInt(readNext[target.getAuxTimestampColumn()]) / 1000 - previousDelta;
                    previousDelta = measDeltaSeconds + previousDelta;
                    if (measDeltaSeconds <= 0) {
                        measDeltaSeconds = 1;
                    }
                }

                //calculate delta
                for (int j = 0; j < measDeltaSeconds; j++) {
                    current = (Second) current.next();
                }

                /**
                 * record start and end of stats markers
                 */
                if (statsStartSecond == null
                        && i * 100 / readAll.size() >= analysis.getSamplesToStripRatio()) {
                    statsStartSecond = new Second(current.getSecond(), current.getMinute());
                }
                if (statsEndSecond == null
                        && i * 100 / readAll.size() >= (100 - analysis.getSamplesToStripRatio())) {
                    statsEndSecond = new Second(current.getSecond(), current.getMinute());
                }

                tSeries.add(current, nexValue);
            } else {
                LOGGER.warn("Attempted invalid column:" + target.getLabel());
            }
        }
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection(tSeries);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(target.getLabel(), "Time", target.getLabel(), timeSeriesCollection, false, false, false);
        addMarkers(chart, statsStartSecond, statsEndSecond);
        chart.addSubtitle(new TextTitle("CollFreq:" + analysis.getSettings().getMeasIntervalSeconds()));
        BufferedImage createBufferedImage = chart.createBufferedImage(320, 240);
        byte[] graph = ChartUtilities.encodeAsPNG(createBufferedImage, false, 9);
        UUID randomUUID = UUID.randomUUID();
        File directory = new File(GRAPHS_LOC);
        if (!directory.exists()) {
            directory.mkdir();
        }
        String path = directory.getPath() + File.separator + target.getLabel() + randomUUID + ".png";
        FileOutputStream stream = new FileOutputStream(path);
        try {
            stream.write(graph);
        } finally {
            stream.close();
        }

        return path;
    }

    private static void addMarkers(JFreeChart chart, Second statsStartSecond, Second statsEndSecond) {
        XYPlot plot = chart.getXYPlot();
        if (statsStartSecond != null) {
            //final Second statsStartSecond = new Second(new Date(analysis.getStartTimeStamp() + statsStart));
            final Marker statsStartMarker = new ValueMarker(statsStartSecond.getFirstMillisecond());
            statsStartMarker.setPaint(Color.GREEN);
            statsStartMarker.setLabel("Stats Start");
            statsStartMarker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
            statsStartMarker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
            plot.addDomainMarker(statsStartMarker);
        }
        if (statsEndSecond != null) {
            //statsEndSecond = new Second(new Date(analysis.getStartTimeStamp() + statsEnd));
            final Marker statsEndMarker = new ValueMarker(statsEndSecond.getFirstMillisecond());
            statsEndMarker.setPaint(Color.GREEN);
            statsEndMarker.setLabel("Stats End");
            statsEndMarker.setLabelAnchor(RectangleAnchor.TOP_LEFT);
            statsEndMarker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
            plot.addDomainMarker(statsEndMarker);
        }
    }

}
