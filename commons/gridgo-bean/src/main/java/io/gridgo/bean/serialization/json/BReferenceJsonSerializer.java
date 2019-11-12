package io.gridgo.bean.serialization.json;

import java.io.IOException;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;

import io.gridgo.bean.BReference;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@SuppressWarnings("rawtypes")
public class BReferenceJsonSerializer implements ReadWriteObject<BReference> {

    @NonNull
    private final BElementJsonSerializer elementSerializer;

    @Override
    public void write(JsonWriter writer, BReference value) {
        if (value == null || value.getReference() == null) {
            writer.writeNull();
            return;
        }

        writer.serializeObject(value.getReference());
    }

    @Override
    public BReference read(JsonReader reader) throws IOException {
        return null;
    }
}
