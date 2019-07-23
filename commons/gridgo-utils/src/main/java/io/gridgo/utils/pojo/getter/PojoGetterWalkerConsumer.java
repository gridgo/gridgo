package io.gridgo.utils.pojo.getter;

public interface PojoGetterWalkerConsumer {

    void accept(String fieldName, Object value);
}
