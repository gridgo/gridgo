package io.gridgo.bean.test.support.supported.ref;

import org.mockito.internal.util.io.IOUtil;

import java.io.InputStream;
import java.io.OutputStream;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BReference;
import io.gridgo.bean.serialization.BSerializationPlugin;
import io.gridgo.bean.serialization.BSerializer;
import io.gridgo.bean.test.support.SimplePojo;

@BSerializationPlugin("simplepojo")
public class MockSimplePojoSerializer implements BSerializer {

    @Override
    public void serialize(BElement element, OutputStream out) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BElement deserialize(InputStream in) {
        return BReference.of(new SimplePojo(IOUtil.readLines(in).iterator().next()));
    }
}
