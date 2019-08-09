package io.gridgo.utils.pojo.setter;

import io.gridgo.utils.pojo.PojoProxy;

public interface PojoSetterProxy extends PojoProxy {

    void applyValue(Object target, String fieldName, Object value);

    void walkThrough(Object target, PojoSetterConsumer setter, String... fields);
}
