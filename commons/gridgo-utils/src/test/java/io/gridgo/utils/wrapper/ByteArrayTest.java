package io.gridgo.utils.wrapper;

import io.gridgo.utils.hash.BinaryHashCodeCalculator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unlikely-arg-type")
public class ByteArrayTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test(expected = NullPointerException.class)
    public void newInstance_ShouldThrowException_WhenByteArrayInputIsNull() {
        ByteArray.newInstance(null);
    }

    @Test(expected = NullPointerException.class)
    public void newInstance_ShouldThrowException_WhenHashCodeCalculatorIsNull() {
        ByteArray.newInstance(new byte[0], null);
    }

    @Test
    public void getHashCodeCalculator_ShouldReturnDEFAULT_WhenInputNotHaveCalculatorId() {
        var x = ByteArray.newInstance(new byte[0]);
        var ans = x.getHashCodeCalculator();
        assertSame(BinaryHashCodeCalculator.DEFAULT, ans);
    }

    @Test
    public void getHashCodeCalculator_WhenUseNewCalculator() {
        var hashCodeCalculator = mock(BinaryHashCodeCalculator.class);
        when(hashCodeCalculator.getId()).thenReturn(100);
        var bytesInput = new byte[0];
        var byteArray = ByteArray.newInstance(bytesInput, hashCodeCalculator);
        assertSame(hashCodeCalculator, byteArray.getHashCodeCalculator());
        assertSame(bytesInput, byteArray.getSource());
    }

    @Test
    public void toBase64() {
        var hashCodeCalculator = mock(BinaryHashCodeCalculator.class);
        when(hashCodeCalculator.getId()).thenReturn(100);
        var bytesInput = new byte[] { 0x00, 0x01, 0x02 };
        var byteArray = ByteArray.newInstance(bytesInput, hashCodeCalculator);
        var ans = byteArray.toBase64();
        assertEquals("AAEC", ans);
        assertEquals("[0, 1, 2]", byteArray.toString());
    }

    @Test
    public void test_equals() {
        var hashCodeCalculator = mock(BinaryHashCodeCalculator.class);
        when(hashCodeCalculator.getId()).thenReturn(100);
        var bytesInput = new byte[] { 0x00, 0x01, 0x02 };
        var byteArray1 = ByteArray.newInstance(bytesInput, hashCodeCalculator);
        assertTrue(byteArray1.equals(new byte[] { 0x00, 0x01, 0x02 }));
        assertTrue(byteArray1.equals(ByteArray.newInstance(new byte[] { 0x00, 0x01, 0x02 })));

        assertFalse(byteArray1.equals(null));
        assertFalse(byteArray1.equals(Long.valueOf(1)));
    }

    @Test
    public void test_compareTo() {
        var byteArray1 = ByteArray.newInstance(new byte[] { 0x00, 0x01, 0x02 });
        var byteArray2 = ByteArray.newInstance(new byte[] { 0x00, 0x01, 0x02 });
        var byteArray3 = ByteArray.newInstance(new byte[] { 0x20, 0x01 });
        var byteArray4 = ByteArray.newInstance(new byte[] { 0x00, 0x01, 0x02, 0x03 });

        assertEquals(0, byteArray1.compareTo(byteArray2));

        // If different, the compared by hashCode()
        assertEquals(1, byteArray1.compareTo(byteArray3));
        assertEquals(-1, byteArray1.compareTo(byteArray4));
    }
}