package io.gridgo.utils.pojo.setter;

public interface PojoSetterProxyBuilder {

    PojoSetterProxy buildSetterProxy(Class<?> type);

    static PojoSetterProxyBuilder newJanino() {
        return new JaninoSetterProxyBuilder();
    }
}
