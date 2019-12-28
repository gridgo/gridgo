package io.gridgo.bean.test;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BValue;

public class BValueUnitTest {

    @Test
    public void testSerialization() {
        var val = BValue.of(0);
        var after = BElement.ofBytes(new String(val.toBytes()).getBytes());
        Assert.assertEquals(0, (int) after.getInnerValue());
    }
}
