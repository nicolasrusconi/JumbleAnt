package com.jumble.report;

import static org.junit.Assert.*; 

import org.junit.Test;


public class FailTest {

    @Test
    public void testDescriptionParsedProperly() throws Exception {
        Fail fail = new Fail("org.jumble.MoveTest:21: negated conditional");
        assertEquals("21", fail.getLine());
        assertEquals("negated conditional", fail.getMutant());
    }
}
