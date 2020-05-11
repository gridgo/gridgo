package io.gridgo.pojo;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import io.gridgo.pojo.builder.PojoSchemaBuilder;
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
    @Ignore
    public void testWrapperArray() {
        var config = PojoSchemaConfig.DEFAULT;
        new PojoSchemaBuilder<>(WrapperArray.class, config).build();
    }

    @Test
    @Ignore
    public void testCollection() {
        var config = PojoSchemaConfig.DEFAULT;
        new PojoSchemaBuilder<>(CollectionPojo.class, config).build();
    }
}
