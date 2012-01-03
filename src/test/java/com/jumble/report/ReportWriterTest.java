package com.jumble.report;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ReportWriterTest {

    @Rule public TemporaryFolder testFolder = new TemporaryFolder();

    private static final String PACKAGE = "org.jumble";
    private static final String CLASS = "XTest";
    private static final String FULL_NAME = PACKAGE + "." + CLASS;
    private JumbleRunReport jumbleRunReport;
    private String reportFolder;

    @Before
    public void setUp() throws Exception {
        jumbleRunReport = new JumbleRunReport();
        List<String> testsMethods = new ArrayList<String>();
        testsMethods.add("testMethod");
        MutatedClassReport report = new MutatedClassReport(FULL_NAME,0, 1);
        jumbleRunReport.add(report);
        testFolder.create();
        reportFolder = testFolder.newFolder("reports").getAbsolutePath() + "/";
    }

    @Test
    public void testReportIsWritten() throws Exception {
        ReportWriter reportWriter = new ReportWriter();

        reportWriter.write(jumbleRunReport, reportFolder);
        File file = new File(reportFolder + "jumbleReport.xml");
        assertTrue(file.exists());
    }

    @Test
    public void testReportFormat() throws Exception {
        ReportWriter reportWriter = new ReportWriter();
        reportWriter.write(jumbleRunReport, reportFolder);
        String content = readFileAsString(reportFolder +"/jumbleReport.xml");

        assertEquals(
                "<testquality score=\"0%\">\n" +
                "  <packages>\n" +
                "    <package name=\"" + PACKAGE + "\" score=\"0%\">\n" +
                "      <classes>\n" +
                "        <class name=\""+ CLASS + "\" time=\"0.0s\" point=\"1\" score=\"0%\">\n" +
                "          <fails/>\n" +
                "        </class>\n" +
                "      </classes>\n" +
                "    </package>\n" +
                "  </packages>\n" +
                "</testquality>", content);
    }

    @Test
    public void testReadReport() throws Exception {
        ReportWriter reportWriter = new ReportWriter();
        reportWriter.write(jumbleRunReport, reportFolder);

        JumbleRunReport readReport = reportWriter.read(reportFolder);

        assertEquals("0%", readReport.getScore());
        assertEquals(1, readReport.getPackages().size());
        assertEquals(PACKAGE, readReport.getPackages().get(0).getName());
    }

    @Test
    public void testMutadedClassScoreIsParsedProperly() throws Exception {
        ReportWriter reportWriter = new ReportWriter();
        MutatedClassReport mutatedClassReport = jumbleRunReport.getPackages().get(0).getReports().get(0);
        mutatedClassReport.add(true,"failed mutation");
        reportWriter.write(jumbleRunReport, reportFolder);

        JumbleRunReport readReport = reportWriter.read(reportFolder);

        mutatedClassReport = readReport.getPackages().get(0).getReports().get(0);
        assertEquals("100%", mutatedClassReport.getFormattedScore());
    }
    private String readFileAsString(String filePath) throws java.io.IOException{
        byte[] buffer = new byte[(int) new File(filePath).length()];
        FileInputStream f = new FileInputStream(filePath);
        f.read(buffer);
        return new String(buffer);
    }

}
