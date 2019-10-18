package io.gridgo.utils.hash;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class BinaryHashCodeCalculatorTest {

    @Test
    public void test_DEFAULT_ShouldReturn0_WithNullInput() {
        assertEquals(0, BinaryHashCodeCalculator.DEFAULT.calcHashCode(null));
        assertEquals(BinaryHashCodeCalculator.ID_DEFAULT, BinaryHashCodeCalculator.DEFAULT.getId());
    }

    @Test
    public void test_DEFAULT_ShouldReturn1_WithEmptyInput() {
        assertEquals(1, BinaryHashCodeCalculator.DEFAULT.calcHashCode(new byte[0]));
    }

    @Test
    public void test_DEFAULT_ShouldSuccess() {
        assertEquals(955331, BinaryHashCodeCalculator.DEFAULT.calcHashCode(new byte[]{0x01, 0x02, 0x03, 0x04}));
    }

    @Test
    public void test_REVERSED_ShouldReturn0_WithNullInput() {
        assertEquals(0, BinaryHashCodeCalculator.REVERSED.calcHashCode(null));
        assertEquals(BinaryHashCodeCalculator.ID_REVERSED, BinaryHashCodeCalculator.REVERSED.getId());

    }

    @Test
    public void test_REVERSED_ShouldReturn1_WithEmptyInput() {
        assertEquals(1, BinaryHashCodeCalculator.REVERSED.calcHashCode(new byte[0]));
    }

    @Test
    public void test_REVERSED_ShouldSuccess() {
        assertEquals(1045631, BinaryHashCodeCalculator.REVERSED.calcHashCode(new byte[]{0x01, 0x02, 0x03, 0x04}));
    }

    @Test(expected = NullPointerException.class)
    public void registerCalculator_ShouldThrowException_WhenInputIsNull() {
        BinaryHashCodeCalculator.registerCalculator(null);
    }

    @Test(expected = RuntimeException.class)
    public void registerCalculator_ShouldThrowException_WhenRegisterACalculatorTwice() {
        BinaryHashCodeCalculator.registerCalculator(BinaryHashCodeCalculator.DEFAULT);
        BinaryHashCodeCalculator.registerCalculator(BinaryHashCodeCalculator.DEFAULT);
    }

    @Test
    public void getRegisteredCalculator() {
        assertSame(BinaryHashCodeCalculator.DEFAULT,
                BinaryHashCodeCalculator.getRegisteredCalculator(BinaryHashCodeCalculator.ID_DEFAULT));

        assertSame(BinaryHashCodeCalculator.REVERSED,
                BinaryHashCodeCalculator.getRegisteredCalculator(BinaryHashCodeCalculator.ID_REVERSED));

        assertSame(BinaryHashCodeCalculator.XXHASH32_JAVA_UNSAFE,
                BinaryHashCodeCalculator.getRegisteredCalculator(BinaryHashCodeCalculator.ID_XXHASH32_JAVA_UNSAFE));

        assertSame(BinaryHashCodeCalculator.XXHASH32_JAVA_SAFE,
                BinaryHashCodeCalculator.getRegisteredCalculator(BinaryHashCodeCalculator.ID_XXHASH32_JAVA_SAFE));

        assertSame(BinaryHashCodeCalculator.XXHASH32_JNI,
                BinaryHashCodeCalculator.getRegisteredCalculator(BinaryHashCodeCalculator.ID_XXHASH32_JNI));
    }


}