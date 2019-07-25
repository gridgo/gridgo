package io.gridgo.utils.pojo.getter;

public interface PojoGetterProxyBuilder {

    PojoGetterProxy buildGetterWalker(Class<?> target);

    static PojoGetterProxyBuilder newJavassist() {
        return new JavassistGetterProxyBuilder();
    }
}
