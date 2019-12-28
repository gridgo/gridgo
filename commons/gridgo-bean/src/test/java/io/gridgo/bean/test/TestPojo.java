package io.gridgo.bean.test;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.assertEquals;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.test.support.Bar;
import io.gridgo.bean.test.support.Foo;
import io.gridgo.bean.test.support.NumberCollectionPojo;

public class TestPojo {

    private Foo original;

    @Before
    public void setup() {
        original = Foo.builder() //
                .intArrayValue(new int[] { 1, 2, 3, 4 }) //
                .doubleValue(0.123) //
                .barValue(Bar.builder() //
                        .bool(true) //
                        .build()) //
                .intArrayList(Arrays.asList( //
                        new int[] { 1, 2, 3 }, //
                        new int[] { 5, 7, 6 })) //
                .longArrayMap(Map.of( //
                        "longarr1", new long[] { 4l, 5l }, //
                        "longarr2", new long[] { 6l, 9l })) //
                .barMap(Map.of( //
                        "key", Bar.builder() //
                                .bool(true) //
                                .map(Map.of("key1", 10)) //
                                .build())) //
                .build();
    }

    @Test
    public void testSerializePojo() {
        BObject originalAsBObject = BObject.ofPojo(original);
        byte[] bytes = originalAsBObject.toBytes();

        Foo rebuilt = BElement.ofBytes(bytes).asObject().toPojo(Foo.class);

        // convert pojo to bobject to execute equals field by field
        assertEquals(originalAsBObject, BObject.ofPojo(rebuilt));
    }

    @Test
    public void testNumberCollectionPojo() {
        var pojo = NumberCollectionPojo.builder() //
                // list
                .byteList(Arrays.asList( //
                        (byte) 1, //
                        (byte) 2, //
                        (byte) 3)) //
                .shortList(Arrays.asList( //
                        (short) 1, //
                        (short) 2, //
                        (short) 3)) //
                .integerList(Arrays.asList( //
                        1, //
                        2, //
                        3)) //
                .longList(Arrays.asList( //
                        (long) 1, //
                        (long) 2, //
                        (long) 3)) //
                .floatList(Arrays.asList( //
                        1.0f, //
                        2.1f, //
                        3.2f)) //
                .doubleList(Arrays.asList( //
                        1.0, //
                        2.1, //
                        3.2)) //
                // set
                .byteSet(new HashSet<>(Arrays.asList( //
                        (byte) 1, //
                        (byte) 2, //
                        (byte) 3))) //
                .shortSet(new HashSet<>(Arrays.asList( //
                        (short) 1, //
                        (short) 2, //
                        (short) 3))) //
                .integerSet(new HashSet<>(Arrays.asList( //
                        1, //
                        2, //
                        3))) //
                .longSet(new HashSet<>(Arrays.asList( //
                        (long) 1, //
                        (long) 2, //
                        (long) 3))) //
                .floatSet(new HashSet<>(Arrays.asList( //
                        1.0f, //
                        2.1f, //
                        3.2f))) //
                .doubleSet(new HashSet<>(Arrays.asList( //
                        1.0, //
                        2.1, //
                        3.2))) //
                // map
                .byteMap(Map.of("1", (byte) 1, //
                        "2", (byte) 2, //
                        "3", (byte) 3)) //
                .shortMap(Map.of("1", (short) 1, //
                        "2", (short) 2, //
                        "3", (short) 3)) //
                .integerMap(Map.of("1", 1, //
                        "2", 2, //
                        "3", 3)) //
                .longMap(Map.of( //
                        "1", (long) 1, //
                        "2", (long) 2, //
                        "3", (long) 3)) //
                .floatMap(Map.of( //
                        "1", (float) 1, //
                        "2", (float) 2, //
                        "3", (float) 3)) //
                .doubleMap(Map.of( //
                        "1", (double) 1, //
                        "2", (double) 2, //
                        "3", (double) 3)) //
                .build();

        var bObj = BObject.ofPojo(pojo);

        var rebuiltPojo = bObj.toPojo(NumberCollectionPojo.class);
        assertEquals(pojo, rebuiltPojo);
    }
}
