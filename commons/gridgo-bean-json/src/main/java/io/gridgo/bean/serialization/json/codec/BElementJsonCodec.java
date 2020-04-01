package io.gridgo.bean.serialization.json.codec;

import java.io.IOException;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;

import io.gridgo.bean.BElement;
import io.gridgo.bean.serialization.json.JsonCompactMode;

@SuppressWarnings("rawtypes")
public class BElementJsonCodec implements JsonCodec<BElement> {

    public static final BElementJsonCodec NO_COMPACT = new BElementJsonCodec(JsonCompactMode.FULL);

    public static final BElementJsonCodec COMPACT = new BElementJsonCodec(JsonCompactMode.COMPACT);

    private final BObjectJsonCodec objectCodec;
    private final BArrayJsonCodec arrayCodec;
    private final BValueJsonCodec valueCodec;
    private final BReferenceJsonCodec referenceCodec;

    private BElementJsonCodec(JsonCompactMode compactMode) {
        objectCodec = new BObjectJsonCodec(this, compactMode);
        arrayCodec = new BArrayJsonCodec(this);
        valueCodec = new BValueJsonCodec();
        referenceCodec = compactMode == JsonCompactMode.COMPACT //
                ? new BReferenceCompactJsonCodec() //
                : new BReferenceJsonCodec();
    }

    @Override
    public void write(JsonWriter writer, BElement value) {
        if (value == null)
            writer.writeNull();
        else if (value.isObject())
            objectCodec.write(writer, value.asObject());
        else if (value.isArray())
            arrayCodec.write(writer, value.asArray());
        else if (value.isValue())
            valueCodec.write(writer, value.asValue());
        else if (value.isReference())
            referenceCodec.write(writer, value.asReference());
    }

    BElement read(JsonReader reader, boolean isBeginning) throws IOException {
        switch (reader.last()) {
        case '{':
            return objectCodec.read(reader);
        case '[':
            return arrayCodec.read(reader);
        default:
            return valueCodec.read(reader, isBeginning);
        }
    }

    @Override
    public BElement read(JsonReader reader) throws IOException {
        return read(reader, true);
    }
}
