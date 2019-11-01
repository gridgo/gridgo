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
        Assert.assertEquals(pojo, ref.getInnerValue());
    }

    @Test
    public void testOfBytes() {
        var pojo = Foo.builder().doubleValue(1.0).intValue(1).stringValue("hello").build();
        var ref = BReference.of(pojo);
        var bytes = ref.toBytes();
        var after = BReference.ofBytes(bytes, "raw", Foo.class);
        Foo foo = after.getReference();
        Assert.assertEquals(1.0, foo.getDoubleValue(), 0);
        Assert.assertEquals(1, foo.getIntValue());
        Assert.assertEquals("hello", foo.getStringValue());
    }
}
