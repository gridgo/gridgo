package io.gridgo.bean.serialization;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import io.gridgo.bean.BElement;
import io.gridgo.bean.exceptions.BeanSerializationException;
import io.gridgo.utils.wrapper.ByteBufferInputStream;
import lombok.NonNull;

public interface BSerializer {

    void serialize(BElement element, OutputStream out);

    BElement deserialize(InputStream in);

    default <T> T deserializeToPojo(InputStream in, @NonNull Class<T> targetType) {
        var ele = deserialize(in);

        if (ele == null)
            return null;

        if (ele.isReference())
            return ele.asReference().getReference();

        if (ele.isObject())
            return ele.asObject().toPojo(targetType);

        throw new BeanSerializationException("Cannot deserialize to " + targetType + " from: " + ele);
    }

    default <T> T deserializeToPojo(byte[] bytes, @NonNull Class<T> targetType) {
        return deserializeToPojo(new ByteArrayInputStream(bytes), targetType);
    }

    default <T> T deserializeToPojo(ByteBuffer buffer, @NonNull Class<T> targetType) {
        return deserializeToPojo(new ByteBufferInputStream(buffer), targetType);
    }
}
