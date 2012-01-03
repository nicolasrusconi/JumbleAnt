package com.jumble.report;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;


public class PackageTest {

    private Package aPackage;

    @Test
    public void testScoreCalculation() throws Exception {
        aPackage = new Package("org.test");
        MutatedClassReport report = createMock(80);

        aPackage.add(report);

        assertEquals(Integer.valueOf(80), aPackage.processScores());
        assertEquals("80%", aPackage.getScore());
    }

    @Test
    public void testScoreCalculationWithMultipleReports() throws Exception {
        aPackage = new Package("org.test");
        MutatedClassReport report = createMock(80);
        MutatedClassReport anotherReport = createMock(60);

        aPackage.add(report);
        aPackage.add(anotherReport);

        assertEquals(Integer.valueOf(70), aPackage.processScores());
        assertEquals("70%", aPackage.getScore());
    }

    private MutatedClassReport createMock(Integer score) {
        MutatedClassReport report = mock(MutatedClassReport.class);
        when(report.getScore()).thenReturn(score);
        return report;
    }
}
