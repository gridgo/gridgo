package io.gridgo.bean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BTypeTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void forName_SholdReturnNull_WhenNotExist() {
        assertNull(BType.forName("NEW_FLOAT"));
    }

    @Test
    public void forName_ShouldReturnBType() {
        assertEquals(BType.NULL, BType.forName("Null"));
    }

    @Test
    public void isNumber() {
        assertTrue(BType.BYTE.isNumber());
        assertTrue(BType.SHORT.isNumber());
        assertTrue(BType.INTEGER.isNumber());
        assertTrue(BType.FLOAT.isNumber());
        assertTrue(BType.LONG.isNumber());
        assertTrue(BType.DOUBLE.isNumber());
        assertTrue(BType.GENERIC_NUMBER.isNumber());
        assertFalse(BType.RAW.isNumber());
    }
}