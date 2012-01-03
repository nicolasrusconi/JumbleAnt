package com.jumble.report;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class JumbleRunReportTest extends TestCase {

    private JumbleRunReport jumbleRunReport;

    @Before
    public void setUp() {
        jumbleRunReport = new JumbleRunReport();
    }

    @Test
    public void testPackageIsAdded() throws Exception {
        MutatedClassReport mutatedClassReport = createMock("com.test.Class");

        jumbleRunReport.add(mutatedClassReport);

        List<Package> packages = jumbleRunReport.getPackages();

        assertEquals(1, packages.size());
        assertSame(mutatedClassReport, packages.get(0).getReports().get(0));
    }

    @Test
    public void testCreatesOnePackageForMultipleClassesInSamePackage() throws Exception {
        MutatedClassReport mutatedClassReport = createMock("com.test.Class");
        MutatedClassReport anotherMutatedClassReport = createMock("com.test.AnotherClass");

        jumbleRunReport.add(mutatedClassReport);
        jumbleRunReport.add(anotherMutatedClassReport);

        List<Package> packages = jumbleRunReport.getPackages();

        assertEquals(1, packages.size());
        assertSame(mutatedClassReport, packages.get(0).getReports().get(0));
        assertSame(anotherMutatedClassReport, packages.get(0).getReports().get(1));
    }

    @Test
    public void testScoreCalculation() throws Exception {
        MutatedClassReport mutatedClassReport = createMock("com.test.Class", 60);
        MutatedClassReport anotherMutatedClassReport = createMock("com.othertest.AnotherClass", 80);

        jumbleRunReport.add(mutatedClassReport);
        jumbleRunReport.add(anotherMutatedClassReport);

        assertEquals("70%", jumbleRunReport.processScores());
    }

    private MutatedClassReport createMock(String className, Integer score) {
        MutatedClassReport report = createMock(className);
        when(report.getScore()).thenReturn(score);
        return report;
    }
    private MutatedClassReport createMock(String className) {
        MutatedClassReport report = mock(MutatedClassReport.class);
        when(report.getClassName()).thenReturn(className);
        return report;
    }
}
