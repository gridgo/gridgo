package io.gridgo.utils.pojo.setter;

public interface PojoSetterProxy {

    String[] getFields();

    void applyValue(Object target, String fieldName, Object value);

    void walkThrough(Object target, PojoSetterConsumer setter, String... fields);
}
