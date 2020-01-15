package io.gridgo.bean.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.gridgo.bean.BType;

public class BTypeTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void forName_ShouldReturnNull_WhenNotExist() {
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