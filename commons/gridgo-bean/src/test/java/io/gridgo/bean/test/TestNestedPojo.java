package io.gridgo.bean.test;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.bean.BObject;
import io.gridgo.bean.test.support.NestedPojo;

public class TestNestedPojo {

    @Test
    public void testToPojo() {
        var obj = BObject.of("id", 1).setAny("child", BObject.of("id", 2));
        var pojo = obj.toPojo(NestedPojo.class);
        Assert.assertEquals(1, pojo.getId());
        Assert.assertEquals(2, pojo.getChild().getId());
    }
}
