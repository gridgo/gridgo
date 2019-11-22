package io.gridgo.bean.pojo;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.function.Supplier;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.utils.pojo.setter.data.GenericData;
import io.gridgo.utils.pojo.setter.data.KeyValueData;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BKeyValueData extends BGenericData implements KeyValueData {

    @AllArgsConstructor
    private static class WrappedEntry implements Entry<String, GenericData> {

        @NonNull
        private final Entry<String, BElement> entry;

        @Override
        public String getKey() {
            return entry.getKey();
        }

        @Override
        public GenericData getValue() {
            return BGenericData.ofAny(entry.getValue());
        }

        @Override
        public GenericData setValue(GenericData value) {
            throw new UnsupportedOperationException();
        }
    }

    @AllArgsConstructor
    private static class WrappedIterator implements Iterator<Entry<String, GenericData>> {

        @NonNull
        private final Iterator<Entry<String, BElement>> it;

        @Override
        public Entry<String, GenericData> next() {
            return new WrappedEntry(it.next());
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }
    }

    @NonNull
    private final BObject value;

    @Override
    public Iterator<Entry<String, GenericData>> iterator() {
        return new WrappedIterator(value.entrySet().iterator());
    }

    @Override
    public GenericData get(String key) {
        return BGenericData.ofAny(value.get(key));
    }

    @Override
    public GenericData getOrDefault(String key, GenericData def) {
        var ele = value.get(key);
        if (ele != null)
            return BGenericData.ofAny(ele);
        return def;
    }

    @Override
    public GenericData getOrTake(String key, @NonNull Supplier<GenericData> onAbsentSupplier) {
        var ele = value.get(key);
        if (ele != null)
            return BGenericData.ofAny(ele);
        return onAbsentSupplier.get();
    }
    
    @Override
    public BElement getBElement() {
        return this.value;
    }
}
