package io.gridgo.bean;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import io.gridgo.bean.exceptions.InvalidTypeException;
import io.gridgo.bean.factory.BFactory;

public interface BArray extends BContainer, List<BElement> {

    static BArray wrap(Collection<?> source) {
        return BFactory.DEFAULT.wrap(source);
    }

    static BArray withHolder(List<BElement> holder) {
        return BFactory.DEFAULT.newArrayWithHolder(holder);
    }

    static BArray ofEmpty() {
        return BFactory.newDefaultArray();
    }

    static BArray of(Object data) {
        return BFactory.DEFAULT.newArray(data);
    }

    static BArray ofSequence(Object... sequence) {
        return BFactory.DEFAULT.newArrayFromSequence(sequence);
    }

    @Override
    default boolean isArray() {
        return true;
    }

    @Override
    default BType getType() {
        return BType.ARRAY;
    }

    default BType typeOf(int index) {
        return this.get(index).getType();
    }

    default BArray addAny(Object obj) {
        this.add(this.getFactory().fromAny(obj));
        return this;
    }

    default BArray addAnySequence(Object... elements) {
        for (Object object : elements) {
            this.addAny(object);
        }
        return this;
    }

    default BArray addAnyAll(Collection<?> collection) {
        for (Object object : collection) {
            this.addAny(object);
        }
        return this;
    }

    default BValue getValue(int index) {
        BElement element = this.get(index);
        if (!element.isValue()) {
            throw new InvalidTypeException("BArray contains element at " + index + " of type " + element.getType()
                    + ", which cannot convert to BValue");
        }
        return element.asValue();
    }

    default BArray getArray(int index) {
        BElement element = this.get(index);
        if (element.isNullValue())
            return null;
        return element.asArray();
    }

    default BObject getObject(int index) {
        BElement element = this.get(index);
        if (element.isNullValue())
            return null;
        return element.asObject();
    }

    default BReference getReference(int index) {
        BElement element = this.get(index);
        if (element.isNullValue())
            return null;
        return element.asReference();
    }

    default Boolean getBoolean(int index) {
        return this.getValue(index).getBoolean();
    }

    default Character getChar(int index) {
        return this.getValue(index).getChar();
    }

    default Byte getByte(int index) {
        return this.getValue(index).getByte();
    }

    default Short getShort(int index) {
        return this.getValue(index).getShort();
    }

    default Integer getInteger(int index) {
        return this.getValue(index).getInteger();
    }

    default Long getLong(int index) {
        return this.getValue(index).getLong();
    }

    default Float getFloat(int index) {
        return this.getValue(index).getFloat();
    }

    default Double getDouble(int index) {
        return this.getValue(index).getDouble();
    }

    default String getString(int index) {
        return this.getValue(index).getString();
    }

    default byte[] getRaw(int index) {
        return this.getValue(index).getRaw();
    }

    default BValue removeValue(int index) {
        return this.remove(index).asValue();
    }

    default BObject removeObject(int index) {
        return this.remove(index).asObject();
    }

    default BArray removeArray(int index) {
        return this.remove(index).asArray();
    }

    @Override
    @SuppressWarnings("unchecked")
    default <T extends BElement> T deepClone() {
        BArray result = ofEmpty();
        for (BElement entry : this) {
            result.addAny(entry.deepClone());
        }
        return (T) result;
    }

    default BArrayOptional asOptional() {
        return new BArrayOptional() {

            @Override
            public BArray getBArray() {
                return BArray.this;
            }
        };
    }

    default List<Object> toList() {
        List<Object> list = new LinkedList<>();
        for (BElement entry : this) {
            list.add(entry.getInnerValue());
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    default <T> T getInnerValue() {
        return (T) toList();
    }
}
