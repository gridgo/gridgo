package io.gridgo.utils.pojo.getter;

import java.util.List;

import io.gridgo.utils.pojo.PojoMethodSignature;

public interface PojoGetterProxy {

    String[] getFields();

    List<PojoMethodSignature> getSignatures();

    Object getValue(Object target, String fieldName);

    void walkThrough(Object target, PojoGetterConsumer consumer, String... fields);
}
