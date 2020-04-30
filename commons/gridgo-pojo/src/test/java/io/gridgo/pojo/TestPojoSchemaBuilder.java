package io.gridgo.pojo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.gridgo.pojo.test.support.PrimitivePojo;

public class TestPojoSchemaBuilder {

    @Before
    public void setup() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void testPrimitive() {
        PojoSchemaBuilder builder = new ReflectivePojoSchemaBuilder();
        builder.build(PrimitivePojo.class);
    }
}
