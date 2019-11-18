package io.gridgo.bean.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.bean.BArray;
import io.gridgo.utils.pojo.PojoJsonUtils;

public class TestBElementPojoHelper {

    @Test
    public void testSimpleArray() {
        var e = PojoJsonUtils.toJsonElement(BArray.of(new int[] { 1, 2, 3, 4 }));
        Assert.assertTrue(e instanceof List);
        Assert.assertEquals(4, ((List<?>) e).size());
    }
}
