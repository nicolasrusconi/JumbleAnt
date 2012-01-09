/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * @author nicolas.rusconi
 **/

package com.jumble;

import java.util.List;

import com.jumble.report.JumbleRunReport;
import com.jumble.report.MutatedClassReport;
import com.jumble.report.ReportWriter;
import com.reeltwo.jumble.fast.JumbleResult;
import com.reeltwo.jumble.fast.MutationResult;
import com.reeltwo.jumble.ui.JumbleListener;
import com.reeltwo.jumble.ui.JumbleScorePrinterListener;

public class XmlAndConsoleReportListener implements JumbleListener {

    public static final String REPORT_DIR = "reportDir";
    private MutatedClassReport mutatedClassReport;
    private JumbleListener consolePrinter;
    private JumbleRunReport jumbleRunReport;

    public XmlAndConsoleReportListener() {
        jumbleRunReport = new JumbleRunReport();
        consolePrinter = new JumbleScorePrinterListener(new UnclosablePrintStream(System.out));
    }

    public void jumbleRunStarted(String className, List<String> testNames) {
        consolePrinter = new JumbleScorePrinterListener(new UnclosablePrintStream(System.out));
        consolePrinter.jumbleRunStarted(className, testNames);
    }

    public void performedInitialTest(JumbleResult result, int mutationCount) {
        mutatedClassReport = new MutatedClassReport(result.getClassName(),result.getTimeoutLength(), mutationCount);
        jumbleRunReport.add(mutatedClassReport);
        consolePrinter.performedInitialTest(result, mutationCount);
    }

    public void jumbleRunEnded() {
        consolePrinter.jumbleRunEnded();
    }

    public void writeReport() {
        jumbleRunReport.processScores();
        new ReportWriter().write(jumbleRunReport, System.getProperty(REPORT_DIR));
    }

    public void finishedMutation(MutationResult res) {
        mutatedClassReport.add(res.isPassed(), res.getDescription());
        consolePrinter.finishedMutation(res);
    }

    public void error(String errorMessage) {
        if (mutatedClassReport != null) {
            mutatedClassReport.addError(errorMessage);
        }
        consolePrinter.error(errorMessage);
    }

}
