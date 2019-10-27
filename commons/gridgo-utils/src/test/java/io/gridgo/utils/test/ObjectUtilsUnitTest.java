package io.gridgo.utils.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import io.gridgo.utils.ObjectUtils;
import io.gridgo.utils.test.support.TestObject;

public class ObjectUtilsUnitTest {

    @Test
    public void testFromMap() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("testInt", 1);
        map.put("testBool", true);
        map.put("testStr", "hello");
        map.put("testArr", new int[] { 1, 2, 3 });

        var innerObj = new TestObject();
        innerObj.setTestInt(2);
        map.put("testObj", innerObj);

        var obj = new TestObject();
        ObjectUtils.assembleFromMap(TestObject.class, obj, map);
        Assert.assertEquals(1, obj.getTestInt());
        Assert.assertEquals(true, obj.isTestBool());
        Assert.assertEquals("hello", obj.getTestStr());
        Assert.assertEquals(2, obj.getTestObj().getTestInt());

        var innerMap = new HashMap<String, Object>();
        innerMap.put("testInt", 2);
        obj.setTestMap(innerMap);

        int x = ObjectUtils.<Integer>getValueByPath(obj, "testObj.testInt");
        Assert.assertEquals(2, x);
    }
}
