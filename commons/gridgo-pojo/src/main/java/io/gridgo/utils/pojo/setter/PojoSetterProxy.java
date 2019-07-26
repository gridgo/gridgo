package io.gridgo.utils.pojo.setter;

import java.util.List;

import io.gridgo.utils.pojo.PojoMethodSignature;

public interface PojoSetterProxy {

    String[] getFields();

    List<PojoMethodSignature> getSignatures();

    void applyValue(Object target, String fieldName, Object value);

    void walkThrough(Object target, PojoSetterConsumer setter, String... fields);
}
