package io.gridgo.utils;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {

    public static <K, V> Map<K, V> newMap(Class<K> keyType, Class<V> valueType) {
        return new HashMap<K, V>();
    }
}
