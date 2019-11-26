package io.gridgo.bean.serialization.json.codec;

import java.io.IOException;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.NumberConverter;
import com.dslplatform.json.ParsingException;

import io.gridgo.bean.BValue;
import io.gridgo.utils.ByteArrayUtils;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@SuppressWarnings("rawtypes")
public class BValueJsonCodec implements JsonCodec<BValue> {

    @Override
    public void write(JsonWriter writer, BValue value) {
        if (value == null || value.isNull()) {
            writer.writeNull();
            return;
        }

        switch (value.getType()) {
        case CHAR:
            writer.writeString(value.getString());
            break;
        case RAW:
            writer.writeString(ByteArrayUtils.toHex(value.getRaw(), "0x"));
            break;
        default:
            writer.serializeObject(value.getData());
            break;
        }
    }

    @Override
    public BValue read(JsonReader reader) throws IOException {
        return _read(reader, true);
    }

    BValue _read(JsonReader reader, boolean isBeginning) throws ParsingException, IOException {
        switch (reader.last()) {
        case 'n':
            var wasNull = false;
            try {
                wasNull = reader.wasNull();
            } catch (ParsingException e) {
                if (isBeginning)
                    return BValue.of(reader.toString());
            }

            if (!wasNull)
                throw reader.newParseErrorAt("Expecting 'null' for false constant", 0);

            return BValue.of(null);
        case 't':
            var wasTrue = false;
            try {
                wasTrue = reader.wasTrue();
            } catch (ParsingException e) {
                if (isBeginning)
                    return BValue.of(reader.toString());
            }

            if (!wasTrue)
                throw reader.newParseErrorAt("Expecting 'true' for false constant", 0);

            return BValue.of(true);
        case 'f':
            var wasFalse = false;
            try {
                wasFalse = reader.wasFalse();
            } catch (ParsingException e) {
                if (isBeginning)
                    return BValue.of(reader.toString());
            }

            if (!wasFalse)
                throw reader.newParseErrorAt("Expecting 'false' for false constant", 0);
            return BValue.of(false);
        case '"':
            try {
                return BValue.of(reader.readString());
            } catch (ParsingException ex) {
                if (isBeginning)
                    return BValue.of(reader.toString());
                throw ex;
            }
        default:
            try {
                return BValue.of(NumberConverter.deserializeNumber(reader));
            } catch (ParsingException ex) {
                if (isBeginning)
                    return BValue.of(reader.toString());
                throw ex;
            }
        }
    }
}
