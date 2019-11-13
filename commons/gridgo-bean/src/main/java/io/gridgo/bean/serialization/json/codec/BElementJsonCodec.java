package io.gridgo.bean.serialization.json.codec;

import java.io.IOException;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;

import io.gridgo.bean.BElement;
import io.gridgo.bean.serialization.json.JsonCompactMode;
import io.gridgo.bean.serialization.json.ReadWriteObject;

@SuppressWarnings("rawtypes")
public class BElementJsonCodec implements ReadWriteObject<BElement> {

    public static final BElementJsonCodec NO_COMPACT = new BElementJsonCodec(JsonCompactMode.FULL);

    public static final BElementJsonCodec COMPACT = new BElementJsonCodec(JsonCompactMode.COMPACT);

    private final BObjectJsonCodec objectCodec;
    private final BArrayJsonCodec arrayCodec;
    private final BValueJsonCodec valueCodec;
    private final BReferenceJsonCodec referenceCodec;

    private BElementJsonCodec(JsonCompactMode compactMode) {
        objectCodec = new BObjectJsonCodec(this, compactMode);
        arrayCodec = new BArrayJsonCodec(this);
        valueCodec = new BValueJsonCodec(this);
        referenceCodec = new BReferenceJsonCodec(this);
    }

    @Override
    public void write(JsonWriter writer, BElement value) {
        if (value == null)
            writer.writeNull();

        if (value.isObject())
            objectCodec.write(writer, value.asObject());

        if (value.isArray())
            arrayCodec.write(writer, value.asArray());

        if (value.isValue())
            valueCodec.write(writer, value.asValue());

        if (value.isReference())
            referenceCodec.write(writer, value.asReference());
    }

    @Override
    public BElement read(JsonReader reader) throws IOException {
        switch (reader.last()) {
        case '{':
            return objectCodec.read(reader);
        case '[':
            return arrayCodec.read(reader);
        default:
            return valueCodec.read(reader);
        }
    }

}
