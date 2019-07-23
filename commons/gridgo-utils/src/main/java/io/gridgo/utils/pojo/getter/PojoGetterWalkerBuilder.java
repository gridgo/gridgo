package io.gridgo.utils.pojo.getter;

import io.gridgo.utils.pojo.javassist.getter.JavassistGetterWalkerBuilder;

public interface PojoGetterWalkerBuilder {

    PojoGetterWalker buildGetterWalker(Class<?> target);

    static PojoGetterWalkerBuilder newJavassist() {
        return new JavassistGetterWalkerBuilder();
    }
}
