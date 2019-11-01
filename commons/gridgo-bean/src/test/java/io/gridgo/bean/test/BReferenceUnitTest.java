package io.gridgo.bean.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import io.gridgo.bean.BReference;
import io.gridgo.bean.test.support.Foo;

public class BReferenceUnitTest {

    @Test
    public void testToJson() {
        var pojo = Foo.builder().doubleValue(1.0).intValue(1).stringValue("hello").build();
        var ref = BReference.of(pojo);
        Map<String, Object> jsonElement = ref.toJsonElement();
        Assert.assertEquals(1.0, jsonElement.get("doubleValue"));
        Assert.assertEquals(1, jsonElement.get("intValue"));
        Assert.assertEquals("hello", jsonElement.get("stringValue"));
    }
}
