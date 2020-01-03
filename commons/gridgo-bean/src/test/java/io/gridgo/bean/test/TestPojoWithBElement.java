package io.gridgo.bean.test;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.BValue;
import io.gridgo.bean.pojo.BElementTranslators;
import io.gridgo.bean.pojo.BGenericData;
import io.gridgo.bean.test.support.PojoWithBElement;
import io.gridgo.bean.test.support.SimplePojo;
import io.gridgo.utils.pojo.setter.data.SimpleKeyValueData;
import io.gridgo.utils.pojo.setter.data.SimplePrimitiveData;
import io.gridgo.utils.pojo.setter.data.SimpleReferenceData;
import io.gridgo.utils.pojo.setter.data.SimpleSequenceData;

public class TestPojoWithBElement {

    private PojoWithBElement pojo;

    private Object obj;

    @Before
    public void setup() {
        obj = new Object();
        pojo = PojoWithBElement.builder() //
                .bValue(BValue.of("this is test text")) //
                .bObject(BObject.ofSequence("key1", "value", "key2", 1, "key3", true)) //
                .bArray(BArray.ofSequence("text", false, 1, 'z')) //
                .bContainer(BObject.ofSequence("key", true)) //
                .bReference(BReference.of(obj)) //
                .build();
        pojo.setBElement(BReference.of(TestPojoWithBElement.class));
    }

    @Test
    public void testToBElementFromBElement() {
        var serialized = BObject.ofPojo(pojo);
        var deserialized = serialized.asObject().toPojo(PojoWithBElement.class);
        assertEquals(pojo, deserialized);
    }

    @Test
    public void testToBElementFromAny() {
        var element = BElementTranslators.toBElement(new SimplePrimitiveData(1), null);
        Assert.assertTrue(element.isValue());
        Assert.assertEquals(1, element.asValue().getData());

        var arr = BElementTranslators.toBElement(new SimpleSequenceData(List.of(1, 2, 3)), null);
        Assert.assertTrue(arr.isArray());
        Assert.assertEquals(List.of(1, 2, 3), arr.asArray().toList());

        var obj = BElementTranslators.toBElement(new SimpleKeyValueData(Map.of("key1", "value1", "key2", "value2")),
                null);
        Assert.assertTrue(obj.isObject());
        Assert.assertEquals(Map.of("key1", "value1", "key2", "value2"), obj.asObject().toMap());

        element = BElementTranslators.toBArray(BGenericData.ofValue(BValue.ofEmpty()), null);
        Assert.assertNull(element);

        element = BElementTranslators.toBElement(null, null);
        Assert.assertNull(element);

        element = BElementTranslators.toBElement(new SimplePrimitiveData(null), null);
        Assert.assertNull(element);
    }

    @Test
    public void testToBArrayFromAny() {
        var arr = BElementTranslators.toBArray(new SimpleSequenceData(List.of(1, 2, 3)), null);
        Assert.assertEquals(List.of(1, 2, 3), arr.toList());

        arr = BElementTranslators.toBArray(BGenericData.ofArray(BArray.ofSequence(1, 2, 3)), null);
        Assert.assertEquals(List.of(1, 2, 3), arr.toList());

        arr = BElementTranslators.toBArray(BGenericData.ofValue(BValue.ofEmpty()), null);
        Assert.assertNull(arr);

        arr = BElementTranslators.toBArray(new SimplePrimitiveData(null), null);
        Assert.assertNull(arr);

        Assert.assertNull(BElementTranslators.toBArray(null, null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToBArrayFromBObject() {
        BElementTranslators.toBArray(BGenericData.ofObject(BObject.ofSequence(1, 2)), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToBArrayFromMap() {
        BElementTranslators.toBArray(new SimpleKeyValueData(Map.of("key", "value")), null);
    }

    @Test
    public void testToBObjectFromAny() {
        var arr = BElementTranslators.toBObject(new SimpleKeyValueData(Map.of("key", "value")), null);
        Assert.assertEquals(Map.of("key", "value"), arr.toMap());

        arr = BElementTranslators.toBObject(BGenericData.ofObject(BObject.of("key", "value")), null);
        Assert.assertEquals(Map.of("key", "value"), arr.toMap());

        var pojo = new SimplePojo("test");

        arr = BElementTranslators.toBObject(new SimpleReferenceData(pojo), null);
        Assert.assertEquals(Map.of("name", "test"), arr.toMap());

        arr = BElementTranslators.toBObject(BGenericData.ofReference(BReference.of(pojo)), null);
        Assert.assertEquals(Map.of("name", "test"), arr.toMap());

        arr = BElementTranslators.toBObject(BGenericData.ofValue(BValue.ofEmpty()), null);
        Assert.assertNull(arr);

        arr = BElementTranslators.toBObject(new SimplePrimitiveData(null), null);
        Assert.assertNull(arr);

        Assert.assertNull(BElementTranslators.toBObject(null, null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToBObjectFromBArray() {
        BElementTranslators.toBObject(BGenericData.ofArray(BArray.ofSequence(1, 2, 3)), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToBObjectFromList() {
        BElementTranslators.toBObject(new SimpleSequenceData(List.of(1, 2, 3)), null);
    }

    @Test
    public void testToBValueFromAny() {
        var arr = BElementTranslators.toBValue(new SimplePrimitiveData("text"), null);
        Assert.assertEquals("text", arr.getData());

        arr = BElementTranslators.toBValue(BGenericData.ofValue(BValue.of("text")), null);
        Assert.assertEquals("text", arr.getData());

        arr = BElementTranslators.toBValue(BGenericData.ofValue(BValue.ofEmpty()), null);
        Assert.assertNull(arr);

        arr = BElementTranslators.toBValue(new SimplePrimitiveData(null), null);
        Assert.assertNull(arr);

        Assert.assertNull(BElementTranslators.toBValue(null, null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToBValueFromBArray() {
        BElementTranslators.toBValue(BGenericData.ofArray(BArray.ofSequence(1, 2, 3)), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToBValueFromList() {
        BElementTranslators.toBValue(new SimpleSequenceData(List.of(1, 2, 3)), null);
    }

    @Test
    public void testToBOContainerFromAny() {
        var arr = BElementTranslators.toBContainer(new SimpleKeyValueData(Map.of("key", "value")), null);
        Assert.assertEquals(Map.of("key", "value"), arr.asObject().toMap());

        arr = BElementTranslators.toBContainer(BGenericData.ofObject(BObject.of("key", "value")), null);
        Assert.assertEquals(Map.of("key", "value"), arr.asObject().toMap());

        arr = BElementTranslators.toBContainer(new SimpleSequenceData(List.of(1, 2, 3)), null);
        Assert.assertEquals(List.of(1, 2, 3), arr.asArray().toList());

        arr = BElementTranslators.toBContainer(BGenericData.ofArray(BArray.ofSequence(1, 2, 3)), null);
        Assert.assertEquals(List.of(1, 2, 3), arr.asArray().toList());

        var pojo = new SimplePojo("test");

        arr = BElementTranslators.toBContainer(new SimpleReferenceData(pojo), null);
        Assert.assertEquals(Map.of("name", "test"), arr.asObject().toMap());

        arr = BElementTranslators.toBContainer(BGenericData.ofReference(BReference.of(pojo)), null);
        Assert.assertEquals(Map.of("name", "test"), arr.asObject().toMap());

        arr = BElementTranslators.toBContainer(BGenericData.ofValue(BValue.ofEmpty()), null);
        Assert.assertNull(arr);

        arr = BElementTranslators.toBContainer(new SimplePrimitiveData(null), null);
        Assert.assertNull(arr);

        Assert.assertNull(BElementTranslators.toBContainer(null, null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToBContainerFromBValue() {
        BElementTranslators.toBContainer(BGenericData.ofValue(BValue.of("text")), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToBContainerFromValue() {
        BElementTranslators.toBContainer(new SimplePrimitiveData("text"), null);
    }

    @Test
    public void testToBReferenceFromAny() {
        var obj = new Object();

        var arr = BElementTranslators.toBReference(new SimpleReferenceData(obj), null);
        Assert.assertEquals(obj, arr.getReference());

        arr = BElementTranslators.toBReference(BGenericData.ofReference(BReference.of(obj)), null);
        Assert.assertEquals(obj, arr.getReference());

        arr = BElementTranslators.toBReference(BGenericData.ofValue(BValue.ofEmpty()), null);
        Assert.assertNull(arr);

        arr = BElementTranslators.toBReference(new SimplePrimitiveData(null), null);
        Assert.assertNull(arr);

        Assert.assertNull(BElementTranslators.toBReference(null, null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToBReferenceFromBArray() {
        BElementTranslators.toBReference(BGenericData.ofArray(BArray.ofSequence(1, 2, 3)), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToBReferenceFromList() {
        BElementTranslators.toBReference(new SimpleSequenceData(List.of(1, 2, 3)), null);
    }
}
