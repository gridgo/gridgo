package io.gridgo.bean.test.support.supported;

import org.mockito.internal.util.io.IOUtil;

import java.io.InputStream;
import java.io.OutputStream;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.serialization.BSerializationPlugin;
import io.gridgo.bean.serialization.BSerializer;

@BSerializationPlugin("simplepojoobj")
public class MockSimplePojoBObjectSerializer implements BSerializer {

    @Override
    public void serialize(BElement element, OutputStream out) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BElement deserialize(InputStream in) {
        return BObject.of("name", IOUtil.readLines(in).iterator().next());
    }
}
