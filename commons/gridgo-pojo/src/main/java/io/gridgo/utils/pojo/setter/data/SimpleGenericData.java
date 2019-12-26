package io.gridgo.utils.pojo.setter.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import io.gridgo.utils.ArrayUtils;
import io.gridgo.utils.PrimitiveUtils;

public abstract class SimpleGenericData implements GenericData {

    @SuppressWarnings("unchecked")
    public static GenericData of(Object data) {
        Class<?> type;
        if (data == null || PrimitiveUtils.isPrimitive(type = data.getClass()))
            return new SimplePrimitiveData(data);

        if (GenericData.class.isInstance(data))
            return (GenericData) data;

        if (Collection.class.isInstance(data))
            return new SimpleSequenceData((Collection<?>) data);

        if (type.isArray()) {
            var list = new ArrayList<>();
            ArrayUtils.foreachArray(data, list::add);
            return new SimpleSequenceData(list);
        }

        if (Map.class.isInstance(data))
            return new SimpleKeyValueData((Map<String, ?>) data);

        return new SimpleReferenceData(data);
    }
}
