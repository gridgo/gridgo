package io.gridgo.utils.pojo.getter;

public interface PojoGetterProxyBuilder {

    PojoGetterProxy buildGetterProxy(Class<?> target);

    static PojoGetterProxyBuilder newJanino() {
        return new JaninoGetterProxyBuilder();
    }
}
