package io.gridgo.bean.test.support.supported.ref;

import org.mockito.internal.util.io.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.gridgo.bean.BElement;
import io.gridgo.bean.serialization.BSerializationPlugin;
import io.gridgo.bean.serialization.BSerializer;
import io.gridgo.utils.exception.RuntimeIOException;

@BSerializationPlugin("refraw")
public class MockRawSerializer implements BSerializer {

    @Override
    public void serialize(BElement element, OutputStream out) {
        try {
            out.write(element.asValue().getRaw());
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    @Override
    public BElement deserialize(InputStream in) {
        return BElement.ofAny(IOUtil.readLines(in).iterator().next());
    }
}
