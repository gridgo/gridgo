package io.gridgo.bean.test;

import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import io.gridgo.bean.BReference;
import io.gridgo.bean.test.support.Bar;
import io.gridgo.bean.test.support.Foo;

public class TestDslCompiledJson {

    private Foo original;

    @Before
    public void setup() {
        original = Foo.builder() //
                .intArrayValue(new int[] { 1, 2, 3, 4 }) //
                .doubleValue(0.123) //
                .barValue(Bar.builder() //
                        .b(true) //
                        .build()) //
                .intArrayList(Arrays.asList( //
                        new int[] { 1, 2, 3 }, //
                        new int[] { 5, 7, 6 })) //
                .longArrayMap(Map.of( //
                        "longarr1", new long[] { 4l, 5l }, //
                        "longarr2", new long[] { 6l, 9l })) //
                .barMap(Map.of( //
                        "key", Bar.builder() //
                                .b(true) //
                                .map(Map.of("key1", 10)) //
                                .build())) //
                .build();
    }

    @Test
    public void testCompiledJsonPojo() {
        var json = BReference.of(original).toJson();
        System.out.println("got json: " + json);
    }

}
