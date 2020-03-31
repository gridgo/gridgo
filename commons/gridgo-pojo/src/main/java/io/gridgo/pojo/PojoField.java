package io.gridgo.pojo;

public interface PojoField {

    String name();

    PojoFieldSetter setter();

    PojoFieldGetter getter();
}
