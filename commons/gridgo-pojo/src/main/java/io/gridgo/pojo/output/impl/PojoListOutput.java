package io.gridgo.pojo.output.impl;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import io.gridgo.pojo.output.PojoSchemaOutput;
import io.gridgo.pojo.output.PojoSequenceOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
public class PojoListOutput<KeyType> implements PojoSequenceOutput {

    @Getter
    private final @NonNull List<Object> list;

    private final @NonNull Function<byte[], KeyType> keyTranslator;

    public PojoListOutput(Function<byte[], KeyType> keyTranslator) {
        this(new LinkedList<Object>(), keyTranslator);
    }

    @Override
    public PojoSchemaOutput openSchema(int index) {
        var map = new HashMap<KeyType, Object>();
        list.add(map);
        return new PojoMapOutput<KeyType>(map, keyTranslator);
    }

    @Override
    public PojoSequenceOutput openSequence(int index) {
        var subList = new LinkedList<>();
        this.list.add(subList);
        return new PojoListOutput<KeyType>(subList, keyTranslator);
    }

    @Override
    public void close() {
        // do nothing
    }

    @Override
    public void writeNull(int index) {
        this.list.add(null);
    }

    @Override
    public void writeBoolean(int index, boolean value) {
        this.list.add(value);
    }

    @Override
    public void writeChar(int index, char value) {
        this.list.add(value);
    }

    @Override
    public void writeByte(int index, byte value) {
        this.list.add(value);
    }

    @Override
    public void writeShort(int index, short value) {
        this.list.add(value);
    }

    @Override
    public void writeInt(int index, int value) {
        this.list.add(value);
    }

    @Override
    public void writeLong(int index, long value) {
        this.list.add(value);
    }

    @Override
    public void writeFloat(int index, float value) {
        this.list.add(value);
    }

    @Override
    public void writeDouble(int index, double value) {
        this.list.add(value);
    }

    @Override
    public void writeString(int index, String value, Charset charset) {
        this.list.add(value);
    }

}
