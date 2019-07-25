package io.gridgo.bean.serialization;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import io.gridgo.bean.BElement;
import io.gridgo.utils.wrapper.ByteBufferInputStream;
import io.gridgo.utils.wrapper.ByteBufferOutputStream;

public interface BSerializer {

    void serialize(BElement element, OutputStream out);

    BElement deserialize(InputStream in, BDeserializationConfig config);

    default BElement deserialize(InputStream in) {
        return this.deserialize(in, null);
    }

    default void serialize(BElement element, ByteBuffer out) {
        this.serialize(element, new ByteBufferOutputStream(out));
    }

    default BElement deserialize(ByteBuffer buffer) {
        return this.deserialize(new ByteBufferInputStream(buffer));
    }

    default BElement deserialize(byte[] bytes) {
        return this.deserialize(new ByteArrayInputStream(bytes));
    }

    default int getMinimumOutputCapactity() {
        return 1024;
    }
}
