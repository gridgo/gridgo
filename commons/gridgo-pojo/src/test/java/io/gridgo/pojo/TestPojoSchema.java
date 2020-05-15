package io.gridgo.pojo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import io.gridgo.pojo.builder.PojoSchemaBuilder;
import io.gridgo.pojo.output.impl.PojoOutputs;
import io.gridgo.pojo.test.support.Bar;
import io.gridgo.pojo.test.support.CollectionPojo;
import io.gridgo.pojo.test.support.CombinedPojo;
import io.gridgo.pojo.test.support.Primitive;
import io.gridgo.pojo.test.support.WrapperArray;

public class TestPojoSchema {

    @Before
    public void setup() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void other() {
    }

    @Test
    @Ignore
    public void testPrimitive() throws NoSuchMethodException, SecurityException {
        var config = PojoSchemaConfig.builder() //
                .includeDefault(true) //
                .includeTag("camel") //
                .build();
        new PojoSchemaBuilder<>(Primitive.class, config).build();
    }

    @Test
    @Ignore
    public void testCombined() {
        var config = PojoSchemaConfig.DEFAULT;
        new PojoSchemaBuilder<>(CombinedPojo.class, config).build();
    }

    @Test
//    @Ignore
    public void testWrapperArray() {
        var config = PojoSchemaConfig.DEFAULT;
        var schema = new PojoSchemaBuilder<>(WrapperArray.class, config).build();
        try (var output = PojoOutputs.newMapOutputString()) {
            schema.serialize(new WrapperArray(), output);
            System.out.println(output.getMap());
        }
    }

    @Test
//    @Ignore
    public void testCollection() {
        var config = PojoSchemaConfig.DEFAULT;
        var schema = new PojoSchemaBuilder<>(CollectionPojo.class, config).build();

        @SuppressWarnings("unchecked")
        var target = CollectionPojo.builder() //
                .array2d(new int[][] { { 0, 1, 2 }, { 3, 4, 5 } }) //
                .listBar(Arrays.asList(Bar.builder().data3(new int[] { 1, 2, 3, 4, 5 }).build())) //
                .map(Map.of("the key", new List[] { List.of(Bar.builder().name("abc-xyz").build()) })) //
                .build();

        try (var output = PojoOutputs.newMapOutputString()) {
            schema.serialize(target, output);
            System.out.println(output.getMap());
        }
    }
}
