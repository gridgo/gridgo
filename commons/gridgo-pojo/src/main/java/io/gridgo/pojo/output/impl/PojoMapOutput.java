package io.gridgo.pojo.output.impl;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;

import io.gridgo.pojo.output.PojoSchemaOutput;
import io.gridgo.pojo.output.PojoSequenceOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
public class PojoMapOutput<KeyType> implements PojoSchemaOutput {

    @Getter
    private final @NonNull Map<KeyType, Object> map;

    private final @NonNull Function<byte[], KeyType> keyTranslator;

    public PojoMapOutput(Function<byte[], KeyType> keyTranslator) {
        this(new HashMap<>(), keyTranslator);
    }

    @Override
    public PojoSchemaOutput openSchema(byte[] key) {
        var subMap = new HashMap<KeyType, Object>();
        this.map.put(keyTranslator.apply(key), subMap);
        return new PojoMapOutput<KeyType>(subMap, keyTranslator);
    }

    @Override
    public PojoSequenceOutput openSequence(byte[] key) {
        var list = new LinkedList<Object>();
        this.map.put(keyTranslator.apply(key), list);
        return new PojoListOutput<KeyType>(list, keyTranslator);
    }

    @Override
    public void close() {
        // do nothing
    }

    @Override
    public void writeNull(byte[] key) {
        this.map.put(keyTranslator.apply(key), null);
    }

    @Override
    public void writeBoolean(byte[] key, boolean value) {
        this.map.put(keyTranslator.apply(key), value);
    }

    @Override
    public void writeChar(byte[] key, char value) {
        this.map.put(keyTranslator.apply(key), value);
    }

    @Override
    public void writeByte(byte[] key, byte value) {
        this.map.put(keyTranslator.apply(key), value);
    }

    @Override
    public void writeShort(byte[] key, short value) {
        this.map.put(keyTranslator.apply(key), value);
    }

    @Override
    public void writeInt(byte[] key, int value) {
        this.map.put(keyTranslator.apply(key), value);
    }

    @Override
    public void writeLong(byte[] key, long value) {
        this.map.put(keyTranslator.apply(key), value);
    }

    @Override
    public void writeFloat(byte[] key, float value) {
        this.map.put(keyTranslator.apply(key), value);
    }

    @Override
    public void writeDouble(byte[] key, double value) {
        this.map.put(keyTranslator.apply(key), value);
    }

    @Override
    public void writeString(byte[] key, String value, Charset charset) {
        this.map.put(keyTranslator.apply(key), value);
    }

}
