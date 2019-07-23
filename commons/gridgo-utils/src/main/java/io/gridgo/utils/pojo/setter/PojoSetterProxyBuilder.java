package io.gridgo.utils.pojo.setter;

import io.gridgo.utils.pojo.javassist.setter.JavassistSetterProxyBuilder;

public interface PojoSetterProxyBuilder {

    PojoSetterProxy buildSetterProxy(Class<?> type);

    static PojoSetterProxyBuilder newJavassist() {
        return new JavassistSetterProxyBuilder();
    }
}
