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

package com.jumble.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import com.thoughtworks.xstream.XStream;

public class ReportWriter {

    private static final String JUMBLE_REPORT_XML = "jumbleReport.xml";
    private XStream xStream;

    public ReportWriter(){
        xStream = new XStream();

        xStream.alias("testquality", JumbleRunReport.class);
        xStream.aliasAttribute(JumbleRunReport.class, "score", "score");
        xStream.omitField(JumbleRunReport.class, "totalScore");
        xStream.omitField(JumbleRunReport.class, "packagesMap");

        xStream.alias("package", Package.class);
        xStream.aliasAttribute(Package.class, "name", "name");
        xStream.aliasAttribute(Package.class, "score", "score");

        xStream.alias("class", MutatedClassReport.class);
        xStream.aliasAttribute(MutatedClassReport.class, "name", "name");
        xStream.aliasAttribute(MutatedClassReport.class, "point", "point");
        xStream.aliasAttribute(MutatedClassReport.class, "time", "time");
        xStream.aliasAttribute(MutatedClassReport.class, "score", "score");
        xStream.omitField(MutatedClassReport.class, "fullClassName");
        xStream.omitField(MutatedClassReport.class, "passCount");

        xStream.alias("fail", Fail.class);
        xStream.aliasAttribute(Fail.class, "line", "line");
        xStream.aliasAttribute(Fail.class, "mutant", "mutant");
    }

    public void write(JumbleRunReport jumbleRunReport, String folderPath) {
        xStream.toXML(jumbleRunReport, createReportFile(folderPath + JUMBLE_REPORT_XML));
    }

    private Writer createReportFile(String filePath) {
        try {
            File reportFile = new File(filePath);
            if (reportFile.exists()) {
                reportFile.delete();
            }
            reportFile.createNewFile();
            return new OutputStreamWriter(new FileOutputStream(reportFile), Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("Error creating jumble report file " + filePath, e);
        }
    }

    public JumbleRunReport read(String reportFolder) throws Exception {
        return read( new FileInputStream(new File(reportFolder + JUMBLE_REPORT_XML)));
    }

    public JumbleRunReport read(InputStream report) throws Exception {
        return (JumbleRunReport) xStream.fromXML(report);
    }
}
