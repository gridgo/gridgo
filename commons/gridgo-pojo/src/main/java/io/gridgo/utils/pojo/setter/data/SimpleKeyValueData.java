package io.gridgo.utils.pojo.setter.data;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
public class SimpleKeyValueData extends SimpleGenericData implements KeyValueData {

    @NonNull
    private final Map<String, ?> map;

    @AllArgsConstructor
    private static class WrappedEntry implements Entry<String, GenericData> {

        @Getter
        private String key;

        @Getter
        private GenericData value;

        @Override
        public GenericData setValue(GenericData value) {
            throw new UnsupportedOperationException("Cannot perform setValue");
        }
    }

    @AllArgsConstructor
    private static class WrappedIterator implements Iterator<Entry<String, GenericData>> {

        @NonNull
        private final Iterator<? extends Entry<String, ?>> it;

        @Override
        public boolean hasNext() {
            return this.it.hasNext();
        }

        @Override
        public Entry<String, GenericData> next() {
            var entry = it.next();
            return new WrappedEntry(entry.getKey(), SimpleGenericData.of(entry.getValue()));
        }

    }

    @Override
    public Iterator<Entry<String, GenericData>> iterator() {
        return new WrappedIterator(map.entrySet().iterator());
    }

    @Override
    public GenericData getOrTake(String key, Supplier<GenericData> onAbsentSupplier) {
        var value = map.get(key);
        if (value == null)
            return onAbsentSupplier.get();
        return SimpleGenericData.of(value);
    }

    @Override
    public GenericData getOrDefault(String key, GenericData def) {
        var value = map.get(key);
        if (value == null)
            return def;
        return SimpleGenericData.of(value);
    }

    @Override
    public GenericData get(String key) {
        return SimpleGenericData.of(map.get(key));
    }
}
