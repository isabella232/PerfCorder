package org.restcomm.perfcorder.analyzer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class StatsCalculator {

    public static Map<AnalysisMeasTarget, AnalysisMeasResults> analyzeTarget(List<String[]> readAll, List<AnalysisMeasTarget> targets, int linesToStrip) throws IOException {
        Map<AnalysisMeasTarget, AnalysisMeasResults> measMap = new HashMap();
        Map<AnalysisMeasTarget, DescriptiveStatistics> statsMap = new HashMap();
        //init empty stats to add values later
        for (AnalysisMeasTarget target : targets) {
            statsMap.put(target, new DescriptiveStatistics());
        }

        for (int i = 0; i < readAll.size() - linesToStrip; i++) {
            String[] readNext = readAll.get(i);
            for (int j = 0; j < targets.size(); j++) {
                AnalysisMeasTarget target = targets.get(j);
                int column = target.getColumn();
                String nextCol = readNext[column];
                double nexValue = target.transformIntoDouble(nextCol);
                if (nexValue != AnalysisMeasTarget.INVALID_STRING) {
                    DescriptiveStatistics stats = statsMap.get(target);
                    stats.addValue(nexValue);
                }
            }
        }

        for (AnalysisMeasTarget target : statsMap.keySet()) {
            String graph = GraphGenerator.generateGraph(target, readAll, linesToStrip);
            AnalysisMeasResults measResults = transformIntoResults(statsMap.get(target), graph);
            measMap.put(target, measResults);
        }
        return measMap;
    }

    private static AnalysisMeasResults transformIntoResults(DescriptiveStatistics stats, String graph) {
        AnalysisMeasResults results = new AnalysisMeasResults();
        results.setMax(stats.getMax());
        results.setMin(stats.getMin());
        results.setPercentile5(stats.getPercentile(5));
        results.setPercentile25(stats.getPercentile(25));
        results.setMedian(stats.getPercentile(50));
        results.setPercentile75(stats.getPercentile(75));
        results.setPercentile95(stats.getPercentile(95));
        results.setSum(stats.getSum());
        results.setSumSquares(stats.getSumsq());
        results.setMean(stats.getMean());
        results.setCount(stats.getN());
        results.setStdDev(stats.getStandardDeviation());
        results.setVariance(stats.getVariance());
        results.setKurtosis(stats.getKurtosis());
        results.setSkewness(stats.getSkewness());
        results.setGeometricMean(stats.getGeometricMean());
        results.setQuadraticMean(stats.getQuadraticMean());

        results.setGraph(graph);
        return results;
    }
}
