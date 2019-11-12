package io.gridgo.bean.serialization.json;

import static io.gridgo.bean.serialization.json.JsonCompactMode.NO_COMPACT;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import io.gridgo.bean.BElement;
import io.gridgo.bean.exceptions.BeanSerializationException;
import io.gridgo.bean.serialization.BSerializationPlugin;
import io.gridgo.utils.exception.RuntimeIOException;
import lombok.NonNull;
import net.minidev.json.parser.ParseException;

@BSerializationPlugin(DefaultJsonSerializer.NAME)
public class DefaultJsonSerializer extends AbstractJsonSerialzier {

    public static final String NAME = "json";

    private final boolean mutable;

    public DefaultJsonSerializer() {
        this(NO_COMPACT, null, true);
    }

    public DefaultJsonSerializer(boolean mutable) {
        this(NO_COMPACT, null, mutable);
    }

    protected DefaultJsonSerializer(JsonCompactMode compressMode) {
        this(compressMode, null, true);
    }

    protected DefaultJsonSerializer(JsonCompactMode compressMode, boolean mutable) {
        this(compressMode, null, mutable);
    }

    protected DefaultJsonSerializer(JsonCompactMode compressMode, JsonParsingMode parsingMode, boolean mutable) {
        super(compressMode, parsingMode);
        this.mutable = mutable;
    }

    @Override
    public void serialize(@NonNull BElement element, @NonNull OutputStream out) {
        var outWriter = new OutputStreamWriter(out);
        try {
            getJsonWriter().writeElement(element, outWriter);
            outWriter.flush();
        } catch (IOException e) {
            throw new RuntimeIOException("Error while write out json", e);
        }
    }

    @Override
    public BElement deserialize(@NonNull InputStream in) {
        try {
            var jsonElement = deserializeToJsonElement(in);
            return mutable ? getFactory().fromAny(jsonElement) : getFactory().wrap(jsonElement);
        } catch (UnsupportedEncodingException | ParseException e) {
            throw new BeanSerializationException("Cannot parse json", e);
        }
    }
}
