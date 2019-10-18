package io.gridgo.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UnsafeUtilsTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void isUnsafeAvailable() {
        assertTrue(UnsafeUtils.isUnsafeAvailable());
    }

    @Test
    public void getOptionalUnsafe() {
        var ans = UnsafeUtils.getOptionalUnsafe();
        assertTrue(ans.isPresent());
    }

    @Test
    public void getObjectAddress() {
        String s = "test addr";
        var ans = UnsafeUtils.getObjectAddress(s);
        var f = String.format("0x%x", ans);
        System.out.println(f);
    }

    @Test
    public void getUnsafe() {
        var ans = UnsafeUtils.getUnsafe();
        assertNotNull(ans);
    }
}