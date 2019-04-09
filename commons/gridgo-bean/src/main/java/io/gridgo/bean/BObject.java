package io.gridgo.bean;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

import io.gridgo.bean.exceptions.BeanSerializationException;
import io.gridgo.bean.exceptions.InvalidTypeException;
import io.gridgo.bean.factory.BFactory;
import io.gridgo.utils.ObjectUtils;
import lombok.NonNull;

public interface BObject extends BContainer, Map<String, BElement> {

    default <T> T toPojo(Class<T> clazz) {
        try {
            return ObjectUtils.fromMap(clazz, this.toMap());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            throw new BeanSerializationException("Exception caught while converting BObject to POJO", e);
        }
    }

    static BObject wrap(Map<?, ?> source) {
        return BFactory.DEFAULT.wrap(source);
    }

    static BObject withHolder(Map<String, BElement> holder) {
        return BFactory.DEFAULT.newObjectWithHolder(holder);
    }

    static BObject ofEmpty() {
        return BFactory.DEFAULT.newObject();
    }

    static BObject of(String name, Object value) {
        return ofEmpty().setAny(name, value);
    }

    static BObject of(Object data) {
        return BFactory.DEFAULT.newObject(data);
    }

    static BObject ofPojo(Object pojo) {
        BObject result = ofEmpty();
        result.putAnyAllPojo(pojo);
        return result;
    }

    static BObject ofPojoRecursive(Object pojo) {
        BObject result = ofEmpty();
        result.putAnyAllPojoRecursive(pojo);
        return result;
    }

    static BObject ofSequence(Object... sequence) {
        return BFactory.DEFAULT.newObjectFromSequence(sequence);
    }

    @Override
    default BType getType() {
        return BType.OBJECT;
    }

    default BType typeOf(String field) {
        if (this.containsKey(field)) {
            return this.get(field).getType();
        }
        return null;
    }

    default Boolean getBoolean(String field) {
        var value = this.getValue(field);
        return value == null ? null : value.getBoolean();
    }

    default Boolean getBoolean(String field, Boolean defaultValue) {
        var value = this.getBoolean(field);
        return value == null ? defaultValue : value;
    }

    default Character getChar(String field) {
        var value = this.getValue(field);
        return value == null ? null : value.getChar();
    }

    default Character getChar(String field, Character defaultValue) {
        var value = this.getChar(field);
        return value == null ? defaultValue : value;
    }

    default Byte getByte(String field) {
        var value = this.getValue(field);
        return value == null ? null : value.getByte();
    }

    default Byte getByte(String field, Number defaultValue) {
        var value = this.getByte(field);
        return value == null ? (defaultValue == null ? null : defaultValue.byteValue()) : value;
    }

    default Short getShort(String field) {
        var value = this.getValue(field);
        return value == null ? null : value.getShort();
    }

    default Short getShort(String field, Number defaultValue) {
        var value = this.getShort(field);
        return value == null ? (defaultValue == null ? null : defaultValue.shortValue()) : value;
    }

    default Integer getInteger(String field) {
        var value = this.getValue(field);
        return value == null ? null : value.getInteger();
    }

    default Integer getInteger(String field, Number defaultValue) {
        var value = this.getInteger(field);
        return value == null ? (defaultValue == null ? null : defaultValue.intValue()) : value;
    }

    default Float getFloat(String field) {
        var value = this.getValue(field);
        return value == null ? null : value.getFloat();
    }

    default Float getFloat(String field, Number defaultValue) {
        var value = this.getFloat(field);
        return value == null ? (defaultValue == null ? null : defaultValue.floatValue()) : value;
    }

    default Long getLong(String field) {
        var value = this.getValue(field);
        return value == null ? null : value.getLong();
    }

    default Long getLong(String field, Number defaultValue) {
        var value = this.getLong(field);
        return value == null ? (defaultValue == null ? null : defaultValue.longValue()) : value;
    }

    default Double getDouble(String field) {
        var value = this.getValue(field);
        return value == null ? null : value.getDouble();
    }

    default Double getDouble(String field, Number defaultValue) {
        var value = this.getDouble(field);
        return value == null ? (defaultValue == null ? null : defaultValue.doubleValue()) : value;
    }

    default String getString(String field) {
        var value = this.getValue(field);
        return value == null ? null : value.getString();
    }

    default String getString(String field, String defaultValue) {
        var value = this.getString(field);
        return value == null ? defaultValue : value;
    }

    default byte[] getRaw(String field) {
        var value = this.getValue(field);
        return value == null ? null : value.getRaw();
    }

    default byte[] getRaw(String field, byte[] defaultValue) {
        var value = this.getRaw(field);
        return value == null ? defaultValue : value;
    }

    default BReference getReference(String field) {
        BElement element = this.get(field);
        if (element != null) {
            if (element.isReference())
                return element.asReference();
            if (!element.isNullValue())
                throw new InvalidTypeException("BObject contains element with type " + element.getType() + " which cannot get as BReference");
        }
        return null;
    }

    default BReference getReference(String field, BReference defaultValue) {
        var value = this.getReference(field);
        return value == null ? defaultValue : value;
    }

    default BValue getValue(String field) {
        BElement element = this.get(field);
        if (element == null)
            return null;
        if (element.isValue())
            return element.asValue();
        throw new InvalidTypeException("BObject contains field " + field + " in type of " + element.getType() + " which cannot convert to BValue");
    }

    default BValue getValue(String field, BValue defaultValue) {
        var value = this.getValue(field);
        return value == null ? defaultValue : value;
    }

    default BValue getValueOrNew(String field, @NonNull Supplier<BValue> bValueSupplier) {
        var value = this.getValue(field);
        return value == null ? bValueSupplier.get() : value;
    }

    default BValue getValueOrEmpty(String field) {
        return getValueOrNew(field, getFactory().getValueSupplier());
    }

    default BObject getObject(String field) {
        BElement element = this.get(field);
        if (element == null || element.isNullValue())
            return null;
        if (element.isObject())
            return element.asObject();
        throw new InvalidTypeException("BObject contains element with type " + element.getType() + " which cannot get as BObject");
    }

    default BObject getObject(String field, BObject defaultValue) {
        var value = this.getObject(field);
        return value == null ? defaultValue : value;
    }

    default BObject getObjectOrNew(String field, @NonNull Supplier<BObject> bObjectSupplier) {
        var value = this.getObject(field);
        return value == null ? bObjectSupplier.get() : value;
    }

    default BObject getObjectOrEmpty(String field) {
        return getObjectOrNew(field, getFactory()::newObject);
    }

    default BArray getArray(String field) {
        BElement element = this.get(field);
        if (element == null || element.isNullValue())
            return null;
        if (element.isArray())
            return element.asArray();
        throw new InvalidTypeException("BObject contains element with type " + element.getType() + " which cannot get as BArray");
    }

    default BArray getArray(String field, BArray defaultValue) {
        var value = this.getArray(field);
        return value == null ? defaultValue : value;
    }

    default BArray getArrayOrNew(String field, @NonNull Supplier<BArray> bArraySupplier) {
        var value = this.getArray(field);
        return value == null ? bArraySupplier.get() : value;
    }

    default BArray getArrayOrEmpty(String field) {
        return getArrayOrNew(field, getFactory()::newArray);
    }

    default BElement putAny(String field, Object data) {
        return this.put(field, this.getFactory().fromAny(data));
    }

    default BElement putAnyIfAbsent(String field, Object data) {
        return this.putIfAbsent(field, this.getFactory().fromAny(data));
    }

    default void putAnyAll(Map<?, ?> map) {
        for (Entry<?, ?> entry : map.entrySet()) {
            this.putAny(entry.getKey().toString(), entry.getValue());
        }
    }

    default BElement putAnyPojo(String name, Object pojo) {
        return this.putAny(name, ObjectUtils.toMap(pojo));
    }

    default BElement putAnyPojoRecursive(String name, Object pojo) {
        return this.putAny(name, ObjectUtils.toMapRecursive(pojo));
    }

    default BElement putAnyPojoIfAbsent(String name, Object pojo) {
        return this.putAnyIfAbsent(name, ObjectUtils.toMap(pojo));
    }

    default BElement putAnyPojoRecursiveIfAbsent(String name, Object pojo) {
        return this.putAnyIfAbsent(name, ObjectUtils.toMapRecursive(pojo));
    }

    default void putAnyAllPojo(Object pojo) {
        this.putAnyAll(ObjectUtils.toMap(pojo));
    }

    default void putAnyAllPojoRecursive(Object pojo) {
        this.putAnyAll(ObjectUtils.toMapRecursive(pojo));
    }

    default void putAnySequence(Object... elements) {
        if (elements != null) {
            if (elements.length % 2 != 0) {
                throw new IllegalArgumentException("Sequence's length must be even");
            }
            for (int i = 0; i < elements.length - 1; i += 2) {
                this.putAny(elements[i].toString(), elements[i + 1]);
            }
        }
    }

    default BElement getOrDefault(String field, Supplier<BElement> supplierForNonPresent) {
        if (this.containsKey(field)) {
            return this.get(field);
        }
        return supplierForNonPresent.get();
    }

    default BObject setAny(String name, Object value) {
        this.putAny(name, value);
        return this;
    }

    default BObject setAnyIfAbsent(String name, Object value) {
        this.putAnyIfAbsent(name, value);
        return this;
    }

    default BObject setAnyPojo(String name, Object pojo) {
        this.putAnyPojo(name, pojo);
        return this;
    }

    default BObject setAnyPojoRecursive(String name, Object pojo) {
        this.putAnyPojoRecursive(name, pojo);
        return this;
    }

    default BObject setAnyPojoIfAbsent(String name, Object pojo) {
        this.putAnyPojoIfAbsent(name, pojo);
        return this;
    }

    default BObject setAnyPojoRecursiveIfAbsent(String name, Object pojo) {
        this.putAnyPojoRecursiveIfAbsent(name, pojo);
        return this;
    }

    default BObject set(String name, @NonNull BElement value) {
        this.put(name, value);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    default <T extends BElement> T deepClone() {
        BObject result = ofEmpty();
        for (Entry<String, BElement> entry : this.entrySet()) {
            result.put(entry.getKey(), entry.getValue().deepClone());
        }
        return (T) result;
    }

    default BObjectOptional asOptional() {
        return new BObjectOptional() {

            @Override
            public BObject getBObject() {
                return BObject.this;
            }
        };
    }

    default Map<String, Object> toMap() {
        Map<String, Object> result = new TreeMap<>();
        for (Entry<String, BElement> entry : this.entrySet()) {
            if (entry.getValue() instanceof BValue) {
                result.put(entry.getKey(), ((BValue) entry.getValue()).getData());
            } else if (entry.getValue() instanceof BArray) {
                result.put(entry.getKey(), ((BArray) entry.getValue()).toList());
            } else if (entry.getValue() instanceof BObject) {
                result.put(entry.getKey(), ((BObject) entry.getValue()).toMap());
            } else if (entry.getValue() instanceof BReference) {
                result.put(entry.getKey(), ((BReference) entry.getValue()).getReference());
            } else {
                if (entry.getValue() == null)
                    continue;
                throw new InvalidTypeException("Found unrecognized BElement implementation: " + entry.getValue().getClass());
            }
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    default Map<String, Object> toJsonElement() {
        Map<String, Object> map = new TreeMap<>();
        for (Entry<String, BElement> entry : this.entrySet()) {
            map.put(entry.getKey(), entry.getValue().toJsonElement());
        }
        return map;
    }
}
