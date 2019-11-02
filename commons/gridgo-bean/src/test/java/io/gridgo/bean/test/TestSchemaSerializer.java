package io.gridgo.bean.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import io.gridgo.bean.BReference;
import io.gridgo.bean.exceptions.BeanSerializationException;
import io.gridgo.bean.factory.BFactory;
import io.gridgo.bean.serialization.AbstractMultiSchemaSerializer;
import io.gridgo.bean.serialization.AbstractSingleSchemaSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class TestSchemaSerializer {

    private AbstractMultiSchemaSerializer<Schema> multiSerializer;

    private AbstractSingleSchemaSerializer<Schema> singleSerializer;

    @Before
    public void setUp() {
        multiSerializer = new DummyMultiSchemaSerializer();
        multiSerializer.setFactory(BFactory.DEFAULT);
        multiSerializer.registerSchema(CustomSchema1.class, 1);
        multiSerializer.registerSchema(CustomSchema2.class, 2);

        singleSerializer = new DummySingleSchemaSerializer();
        singleSerializer.setFactory(BFactory.DEFAULT);
        singleSerializer.setSchema(CustomSchema1.class);
    }

    @Test
    public void testSingle() throws IOException {
        var obj = BReference.of(new CustomSchema1("helloworld"));
        try (var out = new ByteArrayOutputStream()) {
            singleSerializer.serialize(obj, out);
            out.flush();
            var result = out.toByteArray();
            var after = singleSerializer.deserialize(result);
            Assert.assertTrue(after != null && after.isReference());
            Assert.assertTrue(after.asReference().getReference() instanceof CustomSchema1);
            CustomSchema1 deserialized = after.asReference().getReference();
            Assert.assertEquals("helloworld", deserialized.getContent());
        }
    }

    @Test
    public void testMultiSchema() throws IOException {
        var obj = BReference.of(new CustomSchema1("helloworld"));
        try (var out = new ByteArrayOutputStream()) {
            multiSerializer.serialize(obj, out);
            out.flush();
            var result = out.toByteArray();
            var after = multiSerializer.deserialize(result);
            Assert.assertTrue(after != null && after.isReference());
            Assert.assertTrue(after.asReference().getReference() instanceof CustomSchema1);
            CustomSchema1 deserialized = after.asReference().getReference();
            Assert.assertEquals("helloworld", deserialized.getContent());
        }

        obj = BReference.of(new CustomSchema2("helloworld"));
        try (var out = new ByteArrayOutputStream()) {
            multiSerializer.serialize(obj, out);
            out.flush();
            var result = out.toByteArray();
            var after = multiSerializer.deserialize(result);
            Assert.assertTrue(after != null && after.isReference());
            Assert.assertTrue(after.asReference().getReference() instanceof CustomSchema2);
            CustomSchema2 deserialized = after.asReference().getReference();
            Assert.assertEquals("helloworld", deserialized.getContent());
        }
    }

    @Test(expected = BeanSerializationException.class)
    public void testSchemaDeregisterByClass() {
        multiSerializer.deregisterSchema(CustomSchema1.class);
        var obj = BReference.of(new CustomSchema1("helloworld"));
        var out = new ByteArrayOutputStream();
        multiSerializer.serialize(obj, out);
    }

    @Test(expected = BeanSerializationException.class)
    public void testSchemaDeregisterById() {
        multiSerializer.deregisterSchema(1);
        Assert.assertNull(multiSerializer.lookupSchema(1));
        var obj = BReference.of(new CustomSchema1("helloworld"));
        var out = new ByteArrayOutputStream();
        multiSerializer.serialize(obj, out);
    }

    @Getter
    @AllArgsConstructor
    static abstract class Schema {

        private String content;
    }

    static class CustomSchema1 extends Schema {

        public CustomSchema1(String content) {
            super(content);
        }
    }

    static class CustomSchema2 extends Schema {

        public CustomSchema2(String content) {
            super(content);
        }
    }

    class DummySingleSchemaSerializer extends AbstractSingleSchemaSerializer<Schema> {

        @Override
        protected void doSerialize(Schema msgObj, OutputStream out) throws Exception {
            out.write((msgObj.getClass().getName() + "#" + msgObj.getContent()).getBytes());
        }

        @Override
        protected Object doDeserialize(InputStream in) throws Exception {
            var str = new String(in.readAllBytes()).split("#");
            var className = str[0];
            var content = str[1];
            var clazz = Class.forName(className);
            return clazz.getConstructor(String.class).newInstance(content);
        }

    }

    class DummyMultiSchemaSerializer extends AbstractMultiSchemaSerializer<Schema> {

        private Map<Integer, Class<? extends Schema>> map = new HashMap<>();

        public DummyMultiSchemaSerializer() {
            super(Schema.class);
        }

        @Override
        protected void onSchemaRegistered(Class<? extends Schema> schema, int id) {
            map.put(id, schema);
        }

        @Override
        protected void onSchemaDeregistered(Class<? extends Schema> schema, int id) {
            map.remove(id);
        }

        @Override
        protected void doSerialize(Integer id, Schema msgObj, OutputStream out) throws Exception {
            out.write((map.get(id).getName() + "#" + msgObj.getContent()).getBytes());
        }

        @Override
        protected Object doDeserialize(InputStream in, Integer id) throws Exception {
            var str = new String(in.readAllBytes()).split("#");
            Assert.assertEquals(map.get(id).getName(), str[0]);
            var content = str[1];
            var clazz = Class.forName(map.get(id).getName());
            return clazz.getConstructor(String.class).newInstance(content);
        }
    }
}
