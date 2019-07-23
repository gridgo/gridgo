package io.gridgo.utils.pojo.getter;

public interface PojoGetterProxy {

    String[] getFields();

    Object getValue(Object target, String fieldName);

    void walkThroughFields(Object target, PojoGetterConsumer consumer, String... fields);
}
