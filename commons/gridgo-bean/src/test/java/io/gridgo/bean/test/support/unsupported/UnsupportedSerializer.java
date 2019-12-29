package io.gridgo.bean.test.support.unsupported;

import java.io.InputStream;
import java.io.OutputStream;

import io.gridgo.bean.BElement;
import io.gridgo.bean.serialization.BSerializationPlugin;
import io.gridgo.bean.serialization.BSerializer;

@BSerializationPlugin("test")
public class UnsupportedSerializer implements BSerializer {

    public UnsupportedSerializer(String dummy) {

    }

    @Override
    public void serialize(BElement element, OutputStream out) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BElement deserialize(InputStream in) {
        throw new UnsupportedOperationException();
    }
}
