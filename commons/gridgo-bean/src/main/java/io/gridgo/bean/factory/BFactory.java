package io.gridgo.bean.factory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Supplier;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.BValue;
import io.gridgo.bean.exceptions.InvalidTypeException;
import io.gridgo.bean.serialization.BSerializerRegistryAware;
import io.gridgo.utils.ArrayUtils;
import io.gridgo.utils.PrimitiveUtils;
import lombok.NonNull;

@SuppressWarnings("unchecked")
public interface BFactory extends BSerializerRegistryAware {

    static final BFactory DEFAULT = new SimpleBFactory();

    static BFactory newInstance() {
        return new SimpleBFactory();
    }

    static BObject newDefaultObject() {
        return DEFAULT.newObject();
    }

    static BArray newDefaultArray() {
        return DEFAULT.newArray();
    }

    static BValue newDefaultValue() {
        return DEFAULT.newValue();
    }

    Supplier<BReference> getReferenceSupplier();

    Function<Map<String, BElement>, BObject> getObjectSupplier();

    Function<Map<?, ?>, BObject> getWrappedObjectSupplier();

    Function<List<BElement>, BArray> getArraySupplier();

    Function<Collection<?>, BArray> getWrappedArraySupplier();

    Supplier<BValue> getValueSupplier();

    @SuppressWarnings("rawtypes")
    default <T extends BElement> T wrap(Object data) {
        if (BElement.class.isInstance(data))
            return (T) data;

        Class<?> clazz;
        T result;
        if (data == null || PrimitiveUtils.isPrimitive(clazz = data.getClass())) {
            result = (T) newValue(data);
        } else if (Collection.class.isInstance(data)) {
            result = (T) this.getWrappedArraySupplier().apply((Collection) data);
        } else if (clazz.isArray()) {
            final List<Object> list = new ArrayList<>();
            ArrayUtils.foreach(data, entry -> list.add(entry));
            result = (T) this.getWrappedArraySupplier().apply(list);
        } else if (Map.class.isInstance(data)) {
            result = (T) this.getWrappedObjectSupplier().apply((Map<?, ?>) data);
        } else {
            result = (T) newReference(data);
        }

        result.setSerializerRegistry(getSerializerRegistry());
        if (result.isContainer())
            result.asContainer().setFactory(this);

        return result;
    }

    default BReference newReference(Object reference) {
        BReference bReference = newReference();
        bReference.setReference(reference);
        bReference.setSerializerRegistry(getSerializerRegistry());
        return bReference;
    }

    default BReference newReference() {
        return this.getReferenceSupplier().get();
    }

    default BObject newObject() {
        return newObjectWithHolder(new HashMap<>());
    }

    default BObject newObject(Object mapData) {
        if (mapData instanceof BObject) {
            return ((BObject) mapData);
        }

        Map<?, ?> map;
        if (Map.class.isAssignableFrom(mapData.getClass())) {
            map = (Map<?, ?>) mapData;
        } else {
            throw new InvalidTypeException("Cannot create new object from non-map data: " + mapData.getClass());
        }

        BObject result = newObject();
        result.putAnyAll(map);

        return result;
    }

    default BObject newObjectWithHolder(Map<String, BElement> holder) {
        BObject result = this.getObjectSupplier().apply(holder);
        result.setFactory(this);
        result.setSerializerRegistry(this.getSerializerRegistry());
        return result;
    }

    default BObject newObjectFromSequence(Object... elements) {
        if (elements != null && elements.length % 2 != 0) {
            throw new IllegalArgumentException("Sequence's length must be even");
        }

        BObject result = newObject();
        result.putAnySequence(elements);

        return result;
    }

    default BArray newArray() {
        return newArrayWithHolder(new ArrayList<>());
    }

    default BArray newArray(Object src) {
        if (src instanceof BArray) {
            return ((BArray) src);
        }

        BArray array = newArray();
        if (src != null && !ArrayUtils.isArrayOrCollection(src.getClass())) {
            array.addAny(src);
        } else {
            ArrayUtils.foreach(src, array::addAny);
        }

        return array;
    }

    default BArray newArrayFromSequence(Object... elements) {
        BArray result = this.newArray();
        result.addAnySequence(elements);
        return result;
    }

    default BArray newArrayWithHolder(List<BElement> holder) {
        BArray result = this.getArraySupplier().apply(holder);
        result.setFactory(this);
        result.setSerializerRegistry(this.getSerializerRegistry());
        return result;
    }

    default BValue newValue() {
        BValue result = this.getValueSupplier().get();
        result.setSerializerRegistry(this.getSerializerRegistry());
        return result;
    }

    default BValue newValue(Object data) {
        if (data instanceof BValue) {
            return ((BValue) data);
        }

        if (data != null && !(data instanceof byte[]) && !PrimitiveUtils.isPrimitive(data.getClass())) {
            throw new IllegalArgumentException("Cannot create new BValue from non-primitive data");
        }

        BValue result = newValue();
        result.setData(data);
        return result;
    }

    default <T extends BElement> T fromAny(Object obj) {
        Class<?> type;
        if (BElement.class.isInstance(obj)) {
            return (T) ((BElement) obj);
        } else if (obj == null || (obj instanceof byte[]) || PrimitiveUtils.isPrimitive(type = obj.getClass())) {
            return (T) newValue(obj);
        } else if (ArrayUtils.isArrayOrCollection(type)) {
            return (T) newArray(obj);
        } else if (Map.class.isInstance(obj) || Properties.class.isInstance(obj)) {
            return (T) newObject(obj);
        }
        return (T) newReference(obj);
    }

    default <T extends BElement> T fromJson(InputStream inputStream) {
        if (inputStream == null)
            return null;
        return this.fromAny(this.lookupSerializer("json").deserialize(inputStream));
    }

    default <T extends BElement> T fromJson(String json) {
        if (json == null)
            return null;
        return fromJson(new ByteArrayInputStream(json.getBytes(Charset.forName("UTF-8"))));
    }

    default <T extends BElement> T fromBytes(@NonNull InputStream in, String serializerName) {
        return (T) this.lookupOrDefaultSerializer(serializerName).deserialize(in);
    }

    default <T extends BElement> T fromBytes(@NonNull ByteBuffer buffer, String serializerName) {
        return (T) this.lookupOrDefaultSerializer(serializerName).deserialize(buffer);
    }

    default <T extends BElement> T fromBytes(@NonNull byte[] bytes, String serializerName) {
        return (T) this.lookupOrDefaultSerializer(serializerName).deserialize(bytes);
    }

    default <T extends BElement> T fromBytes(@NonNull InputStream in) {
        return (T) this.fromBytes(in, null);
    }

    default <T extends BElement> T fromBytes(@NonNull ByteBuffer buffer) {
        return (T) this.fromBytes(buffer, null);
    }

    default <T extends BElement> T fromBytes(@NonNull byte[] bytes) {
        return (T) this.fromBytes(bytes, null);
    }

    default BFactoryConfigurable asConfigurable() {
        throw new UnsupportedOperationException("Instance of " + this.getClass().getName() + " cannot be used as a "
                + BFactoryConfigurable.class.getSimpleName());
    }
}
