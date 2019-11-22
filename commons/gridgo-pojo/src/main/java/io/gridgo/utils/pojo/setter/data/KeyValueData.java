package io.gridgo.utils.pojo.setter.data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public interface KeyValueData extends GenericData, Iterable<Map.Entry<String, ? extends GenericData>> {

    @Override
    default boolean isKeyValue() {
        return true;
    }

    GenericData get(String key);

    GenericData getOrDefault(String key, GenericData def);

    GenericData getOrTake(String key, Supplier<GenericData> onAbsentSupplier);

    default Map<String, Object> toMap() {
        var map = new HashMap<String, Object>();
        for (var entry : this)
            map.put(entry.getKey(), entry.getValue().getInnerValue());
        return map;
    }

    @Override
    default Object getInnerValue() {
        return this.toMap();
    }
}
