package io.gridgo.bean.serialization.json;

import static io.gridgo.bean.serialization.json.JsonCompactMode.COMPACT;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@SuppressWarnings("rawtypes")
public class BObjectJsonSerializer implements ReadWriteObject<BObject> {

    @NonNull
    private final BElementJsonSerializer compositeSerializer;

    @NonNull
    private final JsonCompactMode compactMode;

    @Override
    public void write(JsonWriter writer, BObject value) {
        if (value == null) {
            writer.writeNull();
            return;
        }

        writer.writeByte(JsonWriter.OBJECT_START);
        final int size = value.size();
        if (size > 0) {
            final Iterator<Map.Entry<String, BElement>> iterator = value.entrySet().iterator();
            Map.Entry<String, BElement> kv = iterator.next();
            writer.writeString(kv.getKey());
            writer.writeByte(JsonWriter.SEMI);
            compositeSerializer.write(writer, kv.getValue());
            for (int i = 1; i < size; i++) {
                kv = iterator.next();
                var val = kv.getValue();
                if ((val == null || val.isNullValue()) && this.compactMode == COMPACT)
                    continue;
                writer.writeByte(JsonWriter.COMMA);
                writer.writeString(kv.getKey());
                writer.writeByte(JsonWriter.SEMI);
                compositeSerializer.write(writer, val);
            }
        }
        writer.writeByte(JsonWriter.OBJECT_END);
    }

    @Override
    public BObject read(JsonReader reader) throws IOException {
        if (reader.last() != '{')
            throw reader.newParseError("Expecting '{' for map start");
        var res = BObject.ofEmpty();
        byte nextToken = reader.getNextToken();
        if (nextToken == '}')
            return res;

        String key = reader.readKey();
        res.put(key, compositeSerializer.read(reader));
        while ((nextToken = reader.getNextToken()) == ',') {
            reader.getNextToken();
            key = reader.readKey();
            res.put(key, compositeSerializer.read(reader));
        }
        if (nextToken != '}')
            throw reader.newParseError("Expecting '}' for map end");
        return res;
    }
}
