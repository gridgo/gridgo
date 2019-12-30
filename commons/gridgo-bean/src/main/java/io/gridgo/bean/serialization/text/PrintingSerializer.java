package io.gridgo.bean.serialization.text;

import java.io.InputStream;
import java.io.OutputStream;

import io.gridgo.bean.BElement;
import io.gridgo.bean.serialization.AbstractBSerializer;
import io.gridgo.bean.serialization.BSerializationPlugin;
import lombok.NonNull;

@BSerializationPlugin(PrintingSerializer.NAME)
public class PrintingSerializer extends AbstractBSerializer {

    public static final String NAME = "print";

    @Override
    public void serialize(@NonNull BElement element, @NonNull OutputStream out) {
        BPrinter.print(out, element);
    }

    @Override
    public BElement deserialize(@NonNull InputStream in) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " support only serialize method");
    }

}
