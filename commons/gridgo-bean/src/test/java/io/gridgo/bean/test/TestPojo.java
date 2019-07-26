package io.gridgo.bean.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.support.BElementPojoHelper;
import io.gridgo.bean.test.support.Bar;
import io.gridgo.bean.test.support.Foo;
import io.gridgo.utils.CollectionUtils;
import io.gridgo.utils.MapUtils;

public class TestPojo {

    @Test
    public void testSerializePojo() {
        Foo original = Foo.builder() //
                .intArrayValue(new int[] { 1, 2, 3, 4 }) //
                .doubleValue(0.123) //
                .barValue(Bar.builder() //
                        .b(true) //
                        .build()) //
                .intArrayList(CollectionUtils.newListBuilder(int[].class) //
                        .add(new int[] { 1, 2, 3 }) //
                        .add(new int[] { 5, 7, 6 }) //
                        .build()) //
                .barMap((MapUtils.newMapStringKeyBuilder(Bar.class) //
                        .put("key", Bar.builder() //
                                .b(true) //
                                .map(MapUtils.newMapStringKeyBuilder(Integer.class) //
                                        .put("key1", 10) //
                                        .build()) //
                                .build()) //
                        .build())) //
                .build();

        BObject originalAsBObject = BObject.ofPojo(original);
        byte[] bytes = originalAsBObject.toBytes();

        Foo rebuilt = BElementPojoHelper.bObjectToPojo(BElement.ofBytes(bytes), Foo.class);

        System.out.println("original: " + BReference.of(original));
        System.out.println("rebuilt : " + BReference.of(rebuilt));

        // convert pojo to bobject to execute equals field by field
        assertEquals(originalAsBObject, BObject.ofPojo(rebuilt));
    }
}
