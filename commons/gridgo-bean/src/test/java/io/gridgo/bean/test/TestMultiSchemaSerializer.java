package io.gridgo.bean.test;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import io.gridgo.bean.BReference;
import io.gridgo.bean.factory.BFactory;
import io.gridgo.bean.serialization.AbstractMultiSchemaSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class TestMultiSchemaSerializer {

    @Test
    public void testMultiSchema() throws IOException {
        var serializer = new DummyMultiSchemaSerializer();
        serializer.setFactory(BFactory.DEFAULT);
        serializer.registerSchema(CustomSchema1.class, 1);
        serializer.registerSchema(CustomSchema2.class, 2);

        var obj = BReference.of(new CustomSchema1("helloworld"));
        var out = new ByteArrayOutputStream();
        serializer.serialize(obj, out);
        out.flush();
        var result = out.toByteArray();
        var after = serializer.deserialize(result);
        Assert.assertTrue(after != null && after.isReference());
        Assert.assertTrue(after.asReference().getReference() instanceof CustomSchema1);
        CustomSchema1 deserialized = after.asReference().getReference();
        Assert.assertEquals("helloworld", deserialized.getContent());
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
