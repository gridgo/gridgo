package io.gridgo.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class UuidUtilsTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test(expected = IllegalArgumentException.class)
    public void bytesToUUID_ShouldThrowException_WhenInputWrongLength() {
        UuidUtils.bytesToUUID(new byte[4]);
    }

    @Test
    public void bytesToUUID_ShouldSuccess() {
        var ans = UuidUtils.bytesToUUID(new byte[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16});
        System.out.println(ans);
        assertEquals(0x0102030405060708L, ans.getMostSignificantBits());
        assertEquals(0x090a0b0c0d0e0f10L, ans.getLeastSignificantBits());

        assertEquals("01020304-0506-0708-090a-0b0c0d0e0f10", ans.toString());
    }

    @Test
    public void bytesToUUIDString() {
        var ans = UuidUtils.bytesToUUIDString(new byte[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16});
        assertEquals("01020304-0506-0708-090a-0b0c0d0e0f10", ans);
    }

    @Test
    public void uuidToBytes() {
        String input = "01020201-0506-0708-090a-0b0c0d0e0f10";
        var ans = UuidUtils.uuidToBytes(input);
        assertArrayEquals(new byte[] {1,2,2,1,5,6,7,8,9,10,11,12,13,14,15,16}, ans);
    }


    @Test
    public void isTimeBaseUUID() {
        var uuid = new UUID(-4351671327100169751L, -5090116416321926079L);
        UuidUtils.isTimeBaseUUID(uuid);
    }

    @Test
    public void getTimeFromUUID() {
        var uuid = new UUID(-4351671327100169751L, -5090116416321926079L);
        var ans = UuidUtils.getTimeFromUUID(uuid);
        assertEquals(1571325677106L, ans);
    }

    @Test
    public void test_randomUuid() {
        var ans = UuidUtils.randomUUID();
        assertEquals(4, ans.version());

        var bytes = UuidUtils.randomUUIDAsBytes();
        assertEquals(16, bytes.length);

        var uuidString = UuidUtils.randomUUIDAsString();
        assertEquals(36, uuidString.length());
    }

    @Test
    public void test_timeBasedUuid() {
        var ans = UuidUtils.timebasedUUID();
        assertEquals(1, ans.version());

        var bytes = UuidUtils.timebasedUUIDAsBytes();
        assertEquals(16, bytes.length);

        var uuidString = UuidUtils.timebasedUUIDAsString();
        assertEquals(36, uuidString.length());
    }

}