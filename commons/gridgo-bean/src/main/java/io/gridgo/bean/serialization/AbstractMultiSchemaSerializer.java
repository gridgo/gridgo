package io.gridgo.bean.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BReference;
import io.gridgo.bean.exceptions.BeanSerializationException;
import io.gridgo.bean.exceptions.SchemaInvalidException;
import io.gridgo.utils.ByteArrayUtils;
import io.gridgo.utils.PrimitiveUtils;
import lombok.Getter;
import lombok.NonNull;

public abstract class AbstractMultiSchemaSerializer<S> extends AbstractBSerializer implements MultiSchemaSerializer<S> {

    private final Map<Class<? extends S>, Integer> schemaToId = new HashMap<>();
    private final Map<Integer, Class<? extends S>> idToSchema = new HashMap<>();

    private final Object registerLock = new Object();

    @Getter
    private final Class<? extends S> superSchema;

    protected AbstractMultiSchemaSerializer(@NonNull Class<S> superSchema) {
        this.superSchema = superSchema;
    }

    @Override
    public void registerSchema(@NonNull Class<? extends S> schema, int id) {
        if (this.superSchema != null && !this.superSchema.isAssignableFrom(schema)) {
            throw new SchemaInvalidException("Cannot register schema of class " + schema);
        }
        synchronized (registerLock) {
            if (!schemaToId.containsKey(schema) && !idToSchema.containsKey(id)) {
                schemaToId.put(schema, id);
                idToSchema.put(id, schema);
                onSchemaRegistered(schema, id);
                return;
            }
        }
        throw new SchemaInvalidException("Schema or id already registered: class=" + schema + ", id=" + id);
    }

    @Override
    public void deregisterSchema(@NonNull Class<? extends S> schema) {
        if (schemaToId.containsKey(schema)) {
            synchronized (registerLock) {
                if (schemaToId.containsKey(schema)) {
                    var id = schemaToId.remove(schema);
                    idToSchema.remove(id);
                    this.onSchemaDeregistered(schema, id);
                }
            }
        }
    }

    @Override
    public void deregisterSchema(int id) {
        if (idToSchema.containsKey(id)) {
            synchronized (registerLock) {
                if (idToSchema.containsKey(id)) {
                    var schema = idToSchema.remove(id);
                    schemaToId.remove(schema);
                    this.onSchemaDeregistered(schema, id);
                }
            }
        }
    }

    @Override
    public Integer lookupId(Class<?> schema) {
        return this.schemaToId.get(schema);
    }

    @Override
    public Class<? extends S> lookupSchema(int id) {
        return this.idToSchema.get(id);
    }

    protected void onSchemaRegistered(Class<? extends S> schema, int id) {
        // do nothing
    }

    protected void onSchemaDeregistered(Class<? extends S> schema, int id) {
        // do nothing
    }

    protected Integer appendSchemaId(@NonNull S msgObj, @NonNull OutputStream out) throws IOException {
        Integer id = this.lookupId(msgObj.getClass());
        if (id == null) {
            throw new SchemaInvalidException("Schema " + msgObj.getClass() + " wasn't registered");
        }
        out.write(ByteArrayUtils.primitiveToBytes(id));
        return id;
    }

    @Override
    public final void serialize(BElement element, OutputStream out) {
        if (!(element instanceof BReference)) {
            throw new BeanSerializationException("HasSchema serializer support only BReference");
        }
        Object ref = element.asReference().getReference();
        if (ref == null || !superSchema.isAssignableFrom(ref.getClass())) {
            throw new BeanSerializationException("Reference object must be instanceof " + superSchema.getName());
        }
        S msgObj = element.asReference().getReference();
        try {
            Integer id = appendSchemaId(msgObj, out);
            doSerialize(id, msgObj, out);
        } catch (Exception e) {
            throw new BeanSerializationException("Cannot write message to output stream", e);
        }
    }

    protected Integer extractSchemaId(InputStream in) throws IOException {
        byte[] idBytes = new byte[4];
        in.read(idBytes);
        var id = PrimitiveUtils.getIntegerValueFrom(idBytes);
        return id;
    }

    @Override
    public final BElement deserialize(InputStream in) {
        try {
            var id = extractSchemaId(in);
            var obj = doDeserialize(in, id);
            return this.getFactory().fromAny(obj);
        } catch (Exception e) {
            throw new BeanSerializationException("Error while reading input stream", e);
        }
    }

    protected abstract void doSerialize(Integer id, S msgObj, OutputStream out) throws Exception;

    protected abstract Object doDeserialize(InputStream in, Integer id) throws Exception;
}
