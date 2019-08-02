package io.gridgo.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

public class MapUtils {

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class MapBuilder<K, V> {

        private @NonNull final Map<K, V> map;

        public MapBuilder<K, V> put(K key, V value) {
            this.map.put(key, value);
            return this;
        }

        public MapBuilder<K, V> putIfAbsent(K key, V value) {
            this.map.putIfAbsent(key, value);
            return this;
        }

        public MapBuilder<K, V> compute(K key, BiFunction<? super K, ? super V, ? extends V> reFn) {
            this.map.compute(key, reFn);
            return this;
        }

        public MapBuilder<K, V> computeIfAbsent(K key, Function<? super K, ? extends V> mFn) {
            this.map.computeIfAbsent(key, mFn);
            return this;
        }

        public MapBuilder<K, V> computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> reFn) {
            this.map.computeIfPresent(key, reFn);
            return this;
        }

        public MapBuilder<K, V> remove(K key) {
            this.map.remove(key);
            return this;
        }

        public MapBuilder<K, V> clear() {
            this.map.clear();
            return this;
        }

        public Map<K, V> build() {
            return this.map;
        }
    }

    public static <K, V> MapBuilder<K, V> newMapBuilder(Map<K, V> holder) {
        return new MapBuilder<K, V>(holder);
    }

    public static <K, V> MapBuilder<K, V> newMapBuilder() {
        return newMapBuilder(new HashMap<K, V>());
    }

    public static <K, V> MapBuilder<K, V> newMapBuilder(Class<K> keyType, Class<V> valueType) {
        return newMapBuilder(new HashMap<K, V>());
    }

    public static <V> MapBuilder<String, V> newMapStringKeyBuilder(Class<V> valueType) {
        return newMapBuilder(String.class, valueType);
    }

    public static <K, V> Map<K, V> newMap(Class<K> keyType, Class<V> valueType) {
        return new HashMap<K, V>();
    }

    public static boolean isMap(Class<?> type) {
        return Map.class.isAssignableFrom(type);
    }
}
