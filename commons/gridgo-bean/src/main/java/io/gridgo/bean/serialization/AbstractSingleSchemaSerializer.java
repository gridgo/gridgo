package io.gridgo.bean.serialization;

import java.io.InputStream;
import java.io.OutputStream;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BReference;
import io.gridgo.bean.exceptions.BeanSerializationException;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractSingleSchemaSerializer<S> extends AbstractBSerializer implements SingleSchemaSerializer<S> {

    @Setter
    @Getter
    private Class<? extends S> schema;

    @Override
    public final void serialize(BElement element, OutputStream out) {
        if (!(element instanceof BReference)) {
            throw new BeanSerializationException("HasSchema serializer support only BReference");
        }
        Object ref = element.asReference().getReference();
        if (ref == null || !schema.isAssignableFrom(ref.getClass())) {
            throw new BeanSerializationException("Reference object must be instanceof " + schema.getName());
        }
        S msgObj = element.asReference().getReference();
        try {
            this.doSerialize(msgObj, out);
        } catch (Exception e) {
            throw new BeanSerializationException("Cannot write message to output stream", e);
        }
    }

    @Override
    public final BElement deserialize(InputStream in) {
        try {
            return BElement.wrapAny(doDeserialize(in));
        } catch (Exception e) {
            throw new BeanSerializationException("Cannot read message from input stream", e);
        }
    }

    protected abstract void doSerialize(S msgObj, OutputStream out) throws Exception;

    protected abstract Object doDeserialize(InputStream in) throws Exception;
}
