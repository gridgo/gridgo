package io.gridgo.bean.serialization.json;

import com.dslplatform.json.*;

import java.io.IOException;

@SuppressWarnings({ "rawtypes", "unchecked" })
@JsonConverter(target = byte.class)
public class ByteDslJsonConverter implements Configuration {

    public static final JsonWriter.WriteObject<Byte> JSON_WRITER = new JsonWriter.WriteObject<Byte>() {
        @Override
        public void write(JsonWriter writer, @Nullable Byte value) {
            if (value == null)
                writer.writeNull();
            else
                NumberConverter.serialize(value, writer);
        }
    };

    public static final JsonReader.ReadObject<Byte> ByteReader = new JsonReader.ReadObject<Byte>() {
        @Override
        public Byte read(JsonReader reader) throws IOException {
            return (byte) NumberConverter.deserializeInt(reader);
        }
    };

    public static final JsonReader.ReadObject<Byte> JSON_READER = new JsonReader.ReadObject<Byte>() {
        @Nullable
        @Override
        public Byte read(JsonReader reader) throws IOException {
            return reader.wasNull() ? null : (byte) NumberConverter.deserializeInt(reader);
        }
    };

    @Override
    public void configure(DslJson json) {
        json.registerWriter(byte.class, JSON_WRITER);
        json.registerReader(byte.class, ByteReader);
        json.registerWriter(Byte.class, JSON_WRITER);
        json.registerReader(Byte.class, JSON_READER);
    }
}