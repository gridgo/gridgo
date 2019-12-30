package io.gridgo.bean.test;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;

public class BArrayUnitTest {

    @Test
    public void testBytes() {
        var obj = BArray.ofSequence(1, 2, 3, 4);
        var clone = BElement.ofBytes(obj.toBytes());
        Assert.assertNotNull(clone);
        Assert.assertTrue(clone.isArray());
        Assert.assertEquals(1, clone.asArray().getInteger(0).intValue());
        Assert.assertEquals(2, clone.asArray().getInteger(1).intValue());
        Assert.assertEquals(3, clone.asArray().getInteger(2).intValue());
        Assert.assertEquals(4, clone.asArray().getInteger(3).intValue());
        Assert.assertEquals(obj, clone);
    }
}
