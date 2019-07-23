package io.gridgo.utils.pojo.getter;

public interface PojoGetterWalker {

    void walkThroughFields(Object target, PojoGetterWalkerConsumer consumer, String... fields);
}
