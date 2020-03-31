package io.gridgo.pojo.otac;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestOtacAccessLevel {

    @Test
    public void testAccessLevelKeyword() {
        assertEquals("private", OtacAccessLevel.PRIVATE.getKeyword().trim());
        assertEquals("protected", OtacAccessLevel.PROTECTED.getKeyword().trim());
        assertEquals("", OtacAccessLevel.PACKAGE.getKeyword().trim());
        assertEquals("public", OtacAccessLevel.PUBLIC.getKeyword().trim());
    }
}
