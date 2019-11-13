package io.gridgo.bean.serialization.json.codec;

import java.io.IOException;
import java.util.Iterator;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.serialization.json.ReadWriteObject;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@SuppressWarnings("rawtypes")
public class BArrayJsonCodec implements ReadWriteObject<BArray> {

    @NonNull
    private final BElementJsonCodec compositeCodec;

    @Override
    public void write(JsonWriter writer, BArray value) {
        if (value == null) {
            writer.writeNull();
            return;
        }

        writer.writeByte(JsonWriter.ARRAY_START);
        final int size = value.size();
        if (size > 0) {
            final Iterator<BElement> iterator = value.iterator();
            compositeCodec.write(writer, iterator.next());
            for (int i = 1; i < size; i++) {
                writer.writeByte(JsonWriter.COMMA);
                compositeCodec.write(writer, iterator.next());
            }
        }
        writer.writeByte(JsonWriter.ARRAY_END);
    }

    @Override
    public BArray read(JsonReader reader) throws IOException {
        if (reader.last() != '[')
            throw reader.newParseError("Expecting '[' for list start");

        var res = BArray.ofEmpty();

        byte nextToken = reader.getNextToken();
        if (nextToken == ']')
            return res;

        res.add(compositeCodec.read(reader));
        while ((nextToken = reader.getNextToken()) == ',') {
            reader.getNextToken();
            res.add(compositeCodec.read(reader));
        }

        if (nextToken != ']')
            throw reader.newParseError("Expecting ']' for list end");

        return res;
    }
}
