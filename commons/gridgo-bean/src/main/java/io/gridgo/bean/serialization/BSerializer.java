package io.gridgo.bean.serialization;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import io.gridgo.bean.BElement;
import io.gridgo.bean.exceptions.BeanSerializationException;
import io.gridgo.utils.wrapper.ByteBufferInputStream;
import io.gridgo.utils.wrapper.ByteBufferOutputStream;
import lombok.NonNull;

public interface BSerializer {

    default int getMinimumOutputCapactity() {
        return 1024;
    }

    /********************************************/
    /** -------------- SERIALIZE ------------- **/
    /********************************************/
    void serialize(BElement element, OutputStream out);

    default void serialize(BElement element, ByteBuffer out) {
        this.serialize(element, new ByteBufferOutputStream(out));
    }

    /********************************************/
    /** ------------- DESERIALIZE ------------ **/
    /********************************************/
    BElement deserialize(InputStream in);

    default BElement deserialize(@NonNull ByteBuffer buffer) {
        return this.deserialize(new ByteBufferInputStream(buffer));
    }

    default BElement deserialize(@NonNull byte[] bytes) {
        return this.deserialize(new ByteArrayInputStream(bytes));
    }

    default <T> T deserializeToPojo(InputStream in, @NonNull Class<T> targetType) {
        BElement ele = this.deserialize(in);
        if (ele.isReference() && ele.asReference().referenceInstanceOf(targetType)) {
            return ele.asReference().getReference();
        }
        if (ele != null && ele.isObject()) {
            return ele.asObject().toPojo(targetType);
        }
        throw new BeanSerializationException("Cannot serialize to pojo from: " + ele);
    }

    default <T> T deserializeToPojo(byte[] bytes, @NonNull Class<T> targetType) {
        return this.deserializeToPojo(new ByteArrayInputStream(bytes), targetType);
    }

    default <T> T deserializeToPojo(ByteBuffer buffer, @NonNull Class<T> targetType) {
        return this.deserializeToPojo(new ByteBufferInputStream(buffer), targetType);
    }
}
