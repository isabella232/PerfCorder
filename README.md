#PerfCorder
============

PerfCorder allows you to record and analyse data during performance testing using Sipp tool.
It is specifically oriented to monitor a Java process that is processing the SIP signalling.

The tool is based on Ant scripts, that allows you to start and stop the recording whenever you like.

The recording will include performance information about the system (io, cpu, VM, net) and the java process (GC....).

Since the environment you are using to do the performance testing is as important as the performance data, several meta data is also saved (java opts,start/end timestamp..).

Analyse target will create stats and graphs about the recorded data to help you interpreting the performance test results.

Finally the tool will package all the collected and analysed data into a single zip file.

The tool encourages a default directory layout following Maven convention over configuration principles.
This is the current dir layout:

target

    --->data

        --->meta (decicated to save information about the performance test itself)

            ---><start/endTime>

            ---><copyOfsippScript>

            ---><jar files mounted by java process>

            ---><JVM options used to start the java process>

        --->conf (save files that change java process behavior)

        --->periodic (save perf data every N seconds)

            --->sys

                --->iostat/vmstat/netstat

            --->java
                ---> GCCollection

                ---> jstat

                ---> CPU/Mem/GC CPU
            --->sip

                ---> sipp_stat.csv

                ---> sipp_rtt.csv


#Documentation
========
##How to run SIPP Report tool:

1. Compile with 'mvn install'
2. Copy the file 'target/sipp-report-0.0.1-SNAPSHOT-with-dependencies.jar' to the location of the statistics files produced by SIPP
3. Run with 'java -jar' and check the available options with '-h' flag
4. Have fun

##How to make SIPP produce statistics files

1. When starting SIPP use the following flags '-fd 1 -trace_stat'
2. This will produce a .csv file that can be used by this tool

##How to run collect tool:

1. Install prerequesites (sysstat, java, ant)
2. Download precompiled PerfCorder binaries from (both jar files) [PerfCorder CI](https://mobicents.ci.cloudbees.com/job/PerfCorder/lastSuccessfulBuild/artifact/target/)
3. Extract contents of small jar into current dir, so you get access to CLI tools.
4. Modify perfTools.properties ans set (Java PID, sipp script filename, folder where java conf is saved...).
5. Run 'ant start', from the download directory
6. Run your sipp script
7. When the test is finished, Run 'ant stopAndCompress'

##How to run analysis tool:

1. Run "`pc_analyse.sh <zipfile> <percentageToStripFromCSVs>`"
2. An XML file with all the stats will be printed to std out

##How to run test tool:

1. Run "`cat <analysis_xml_file> | pc_analyse <goals_xsl_file>`"
2. A JUnit XML report file is printed in standard output with test results.


#Coming Soon
========
- [ ] Diff script to compare different collection files.
- [ ] HTML summary view generation from analysis file.
- [ ] Publish REST API for cloud deployment.

#Want to Contribute ?
========
[See our Contributors Guide](https://github.com/Mobicents/sip-servlets/wiki/Contribute-to-Mobicents-SIP-Servlets)

#Issue Tracking and Roadmap
========
[Issue Tracker](https://github.com/Mobicents/PerfCorder/issues)

#Questions ?
========
Please ask your question on [StackOverflow](http://stackoverflow.com/search?q=mobicents) or the Google [public forum](http://groups.google.com/group/mobicents-public)

#License
========

PerfCorder is lead by [TeleStax](http://www.telestax.com/), Inc. and developed collaboratively by a community of individual and enterprise contributors.

PerfCorder is licensed under dual license policy. The default license is the Free Open Source GNU Affero GPL v3.0. Alternatively a commercial license can be obtained from Telestax ([contact form](http://www.telestax.com/contactus/#InquiryForm))

#Continuous Integration and Delivery
========
[PerfCorder CI](https://mobicents.ci.cloudbees.com/job/PerfCorder/lastSuccessfulBuild/artifact/target/)

#Acknowledgements
========
[See who has been contributing to RestComm](http://www.telestax.com/opensource/acknowledgments/)
