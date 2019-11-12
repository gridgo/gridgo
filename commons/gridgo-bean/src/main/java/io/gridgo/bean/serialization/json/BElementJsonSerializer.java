package io.gridgo.bean.serialization.json;

import java.io.IOException;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;

import io.gridgo.bean.BElement;

@SuppressWarnings("rawtypes")
public class BElementJsonSerializer implements ReadWriteObject<BElement> {

    public static final BElementJsonSerializer NO_COMPACT = new BElementJsonSerializer(JsonCompactMode.FULL);

    public static final BElementJsonSerializer COMPACT = new BElementJsonSerializer(JsonCompactMode.COMPACT);

    private final BObjectJsonSerializer objectSerializer;
    private final BArrayJsonSerializer arraySerializer;
    private final BValueJsonSerializer valueSerializer;
    private final BReferenceJsonSerializer referenceSerializer;

    private BElementJsonSerializer(JsonCompactMode compactMode) {
        objectSerializer = new BObjectJsonSerializer(this, compactMode);
        arraySerializer = new BArrayJsonSerializer(this);
        valueSerializer = new BValueJsonSerializer(this);
        referenceSerializer = new BReferenceJsonSerializer(this);
    }

    @Override
    public void write(JsonWriter writer, BElement value) {
        if (value == null)
            writer.writeNull();

        if (value.isObject())
            objectSerializer.write(writer, value.asObject());

        if (value.isArray())
            arraySerializer.write(writer, value.asArray());

        if (value.isValue())
            valueSerializer.write(writer, value.asValue());

        if (value.isReference())
            referenceSerializer.write(writer, value.asReference());
    }

    @Override
    public BElement read(JsonReader reader) throws IOException {
        switch (reader.last()) {
        case '{':
            return objectSerializer.read(reader);
        case '[':
            return arraySerializer.read(reader);
        default:
            return valueSerializer.read(reader);
        }
    }

}
