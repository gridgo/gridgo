package io.gridgo.utils.hash;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FixedValueHashCodeCalculatorTest {

    @Test
    public void calcHashCode() {
        int val = 100;
        var x = new FixedValueHashCodeCalculator<Object>(val);
        assertEquals(val, x.calcHashCode(new Object()));
    }
}