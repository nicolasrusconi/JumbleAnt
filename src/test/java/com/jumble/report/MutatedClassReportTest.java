package com.jumble.report;

import static org.junit.Assert.*; 

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class MutatedClassReportTest {

    private static final String CLASSNAME = "org.test.XTest";
    private static final Boolean FAILED = Boolean.FALSE;
    private static final Boolean PASSED = Boolean.TRUE;
    private MutatedClassReport mutatedClassReport;

    @Before
    public void setup(){
        mutatedClassReport = new MutatedClassReport(CLASSNAME, 1, 1);
    }

    @Test
    public void testFailedMutationIsAddedAsFail() throws Exception {
        mutatedClassReport.add(FAILED, CLASSNAME+":1: - -> +");

        List<Fail> fails = mutatedClassReport.getFails();
        assertEquals(1, fails.size());
        assertEquals("- -> +", fails.get(0).getMutant());
    }

    @Test
    public void testPassedMutationIsNotAddedAsFail() throws Exception {
        mutatedClassReport.add(PASSED, CLASSNAME+":1: - -> +");

        assertTrue(mutatedClassReport.getFails().isEmpty());
    }

    @Test
    public void testPointsHasMutationCountValue() throws Exception {
        mutatedClassReport = new MutatedClassReport(CLASSNAME, 1, 5);

        assertEquals(Integer.valueOf(5), mutatedClassReport.getPoint());
    }

    @Test
    public void testScoreCalculation() throws Exception {
        mutatedClassReport = new MutatedClassReport(CLASSNAME, 1, 5);

        assertEquals(Integer.valueOf(0), mutatedClassReport.getScore());

        mutatedClassReport.add(PASSED, CLASSNAME+":1: - -> +");
        assertScore(mutatedClassReport, 20);

        mutatedClassReport.add(PASSED, CLASSNAME+":1: - -> +");
        assertScore(mutatedClassReport, 40);
    }

    @Test
    public void testScoreCalculationWithPassedMutations() throws Exception {
        mutatedClassReport = new MutatedClassReport(CLASSNAME, 1, 5);
        assertScore(mutatedClassReport, 0);

        mutatedClassReport.add(FAILED, CLASSNAME+":1: - -> +");
        assertScore(mutatedClassReport, 0);

        mutatedClassReport.add(FAILED, CLASSNAME+":1: - -> +");
        assertScore(mutatedClassReport, 0);
    }

    @Test
    public void testScoreCalculationWithZeroPoints() throws Exception {
        mutatedClassReport = new MutatedClassReport(CLASSNAME, 1, 0);

        assertScore(mutatedClassReport, 100);
    }

    private void assertScore(MutatedClassReport mutatedClassReport, int expectedScore) {
        assertEquals(Integer.valueOf(expectedScore), mutatedClassReport.getScore());
        assertEquals(expectedScore + "%", mutatedClassReport.getFormattedScore());
    }
}
