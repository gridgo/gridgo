package io.gridgo.utils.pojo.setter.data;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.function.Supplier;

import io.gridgo.utils.pojo.PojoUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

public interface ReferenceData extends KeyValueData {

    @Override
    default boolean isNull() {
        return this.getReference() == null;
    }

    @Override
    default boolean isReference() {
        return true;
    }

    Object getReference();

    default Class<?> getReferenceClass() {
        var obj = getReference();
        return obj == null ? null : obj.getClass();
    }

    @Override
    default Object getInnerValue() {
        return this.getReference();
    }

    @Override
    default GenericData get(String key) {
        return new SimpleReferenceData(PojoUtils.getValue(getReference(), key));
    }

    @Override
    default GenericData getOrDefault(String key, GenericData def) {
        var result = get(key);
        return result == null ? def : result;
    }

    @Override
    default GenericData getOrTake(String key, Supplier<GenericData> onAbsentSupplier) {
        var result = get(key);
        return result == null ? onAbsentSupplier.get() : result;
    }

    @Override
    default Iterator<Entry<String, GenericData>> iterator() {
//        var type = getReferenceClass();
//        if (type == null)
//            return null;
//        var proxy = PojoGetterRegistry.DEFAULT.getGetterProxy(type);
//        var count = new AtomicInteger(proxy.getFields().length);
//        return new Iterator<Map.Entry<String, GenericData>>() {
//
//            @Override
//            public Entry<String, GenericData> next() {
//                return ;
//            }
//
//            @Override
//            public boolean hasNext() {
//                return count.decrementAndGet() > 0;
//            }
//        };
        return null;
    }
}

@AllArgsConstructor
class SimpleReferenceData implements ReferenceData {

    @Getter
    private final Object reference;

}