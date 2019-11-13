package io.gridgo.bean.serialization.json.codec;

import java.io.IOException;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.NumberConverter;

import io.gridgo.bean.BValue;
import io.gridgo.bean.serialization.json.ReadWriteObject;
import io.gridgo.utils.ByteArrayUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@SuppressWarnings("rawtypes")
public class BValueJsonCodec implements ReadWriteObject<BValue> {

    @NonNull
    private final BElementJsonCodec elementCodec;

    @Override
    public void write(JsonWriter writer, BValue value) {
        if (value == null || value.isNull()) {
            writer.writeNull();
            return;
        }
        var data = value.getData();
        if (Character.class.isInstance(data)) {
            writer.writeString(value.getString());
            return;
        }

        if (byte[].class.isInstance(data)) {
            writer.writeString(ByteArrayUtils.toHex(value.getRaw(), "0x"));
            return;
        }

        writer.serializeObject(data);
    }

    @Override
    public BValue read(JsonReader reader) throws IOException {
        switch (reader.last()) {
        case 'n':
            if (!reader.wasNull()) {
                throw reader.newParseErrorAt("Expecting 'null' for null constant", 0);
            }
            return BValue.of(null);
        case 't':
            if (!reader.wasTrue()) {
                throw reader.newParseErrorAt("Expecting 'true' for true constant", 0);
            }
            return BValue.of(true);
        case 'f':
            if (!reader.wasFalse()) {
                throw reader.newParseErrorAt("Expecting 'false' for false constant", 0);
            }
            return BValue.of(false);
        case '"':
            return BValue.of(reader.readString());
        default:
            return BValue.of(NumberConverter.deserializeNumber(reader));
        }
    }
}
