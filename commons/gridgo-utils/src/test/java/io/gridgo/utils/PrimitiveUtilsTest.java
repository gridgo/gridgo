package io.gridgo.utils;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

public class PrimitiveUtilsTest {

    @Test
    public void testBigDecimal() {
        BigDecimal value;

        value = PrimitiveUtils.getBigDecimalFrom("1000000000.00001");
        assertEquals("1000000000.00001", value.toString());

        value = PrimitiveUtils.getBigDecimalFrom(1000000000.00001);
        assertEquals("1000000000.00001", value.toString());

        value = PrimitiveUtils.getBigDecimalFrom((int) 1000000000);
        assertEquals("1000000000", value.toString());

        value = PrimitiveUtils.getBigDecimalFrom(1_000_000_000_000l);
        assertEquals("1000000000000", value.toString());
    }
}
