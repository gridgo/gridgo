package io.gridgo.bean.test;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BObject;
import io.gridgo.bean.serialization.json.writer.CompositeJsonWriter;
import io.gridgo.utils.pojo.PojoJsonUtils;

public class TestBElementPojoHelper {

    private static final CompositeJsonWriter JSON_WRITER = CompositeJsonWriter.getNoCompressInstance();

    @Test
    public void testSimpleArray() {
        var e = PojoJsonUtils.toJsonElement(BArray.of(new int[] { 1, 2, 3, 4 }));
        Assert.assertTrue(e instanceof List);
        Assert.assertEquals(4, ((List<?>) e).size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBArray() {
        var e = JSON_WRITER.toJsonElement(BArray.ofSequence(1, "2", BObject.of("k", "v"), new int[] { 3, 4 }));
        Assert.assertTrue(e instanceof List);
        var list = (List<?>) e;
        Assert.assertEquals(4, list.size());
        Assert.assertEquals(1, list.get(0));
        Assert.assertEquals("2", list.get(1));
        Assert.assertTrue(list.get(2) instanceof Map);
        var map = (Map<String, ?>) list.get(2);
        Assert.assertEquals("v", map.get("k"));
        Assert.assertTrue(list.get(3) instanceof List);
        var innerList = (List<?>) list.get(3);
        Assert.assertEquals(2, innerList.size());
        Assert.assertEquals(3, innerList.get(0));
        Assert.assertEquals(4, innerList.get(1));
    }
}
