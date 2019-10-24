package io.gridgo.utils.pojo.getter;

import io.gridgo.utils.pojo.PojoProxy;

public interface PojoGetterProxy extends PojoProxy {

    Object getValue(Object target, String fieldName);

    void walkThrough(Object target, PojoGetterConsumer consumer, String... fields);
}
