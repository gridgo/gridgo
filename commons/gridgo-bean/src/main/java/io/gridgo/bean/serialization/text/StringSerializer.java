package io.gridgo.bean.serialization.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BValue;
import io.gridgo.bean.exceptions.BeanSerializationException;
import io.gridgo.bean.serialization.BSerializationPlugin;
import io.gridgo.bean.serialization.BSerializer;
import io.gridgo.utils.exception.RuntimeIOException;

@BSerializationPlugin(StringSerializer.NAME)
public class StringSerializer implements BSerializer {

    public static final String NAME = "string";

    @Override
    public void serialize(BElement element, OutputStream out) {
        if (!element.isValue())
            throw new BeanSerializationException(String.format("Bean of type [%s] cannot be serialized as String", element.getType()));
        try {
            out.write(element.asValue().getRaw());
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    @Override
    public BElement deserialize(InputStream in) {
        try {
            return BValue.of(in.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }
}
