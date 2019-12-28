package io.gridgo.bean.test.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.gridgo.bean.BElement;
import io.gridgo.bean.serialization.BSerializationPlugin;
import io.gridgo.bean.serialization.BSerializer;
import io.gridgo.utils.exception.RuntimeIOException;

@BSerializationPlugin("json")
public class MockJsonSerializer implements BSerializer {

    @Override
    public void serialize(BElement element, OutputStream out) {
        try {
            out.write("test".getBytes());
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    @Override
    public BElement deserialize(InputStream in) {
        return BElement.ofAny("test");
    }
}
