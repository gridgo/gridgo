package io.gridgo.bean.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BObject;
import io.gridgo.bean.support.BElementPojoHelper;

public class TestBElementPojoHelper {

    @Test
    public void testSimpleArray() {
        var e = BElementPojoHelper.anyToJsonElement(new int[] { 1, 2, 3, 4 });
        Assert.assertTrue(e instanceof List);
        Assert.assertEquals(4, ((List<?>) e).size());
    }

    @Test
    public void testBArray() {
        assertList(BElementPojoHelper.anyToJsonElement(new Object[] {1, "2", BObject.of("k", "v"), new int[] { 3, 4 }}));
        assertList(BElementPojoHelper.anyToJsonElement(BArray.ofSequence(1, "2", BObject.of("k", "v"), new int[] { 3, 4 })));
    }

    @SuppressWarnings("unchecked")
    private void assertList(Object e) {
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
