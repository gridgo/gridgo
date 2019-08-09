package io.gridgo.utils.pojo.setter;

import io.gridgo.utils.pojo.AbstractProxyRegistry;
import io.gridgo.utils.pojo.PojoMethodSignature;
import lombok.Getter;

class PojoSetterRegistryImpl extends AbstractProxyRegistry<PojoSetterProxy> implements PojoSetterRegistry {

    @Getter
    private final static PojoSetterRegistryImpl instance = new PojoSetterRegistryImpl();

    private final PojoSetterProxyBuilder setterProxyBuilder = PojoSetterProxyBuilder.newJavassist();

    private PojoSetterRegistryImpl() {
    }

    @Override
    public PojoSetterProxy getSetterProxy(Class<?> type) {
        return this.getProxy(type);
    }

    @Override
    protected PojoSetterProxy buildMandatory(Class<?> type) {
        return setterProxyBuilder.buildSetterProxy(type);
    }

    @Override
    protected void setFieldProxy(PojoMethodSignature signature, PojoSetterProxy proxy) {
        setSetterProxy(signature, proxy);
    }

    @Override
    protected void setFieldElementProxy(PojoMethodSignature signature, PojoSetterProxy proxy) {
        setElementSetterProxy(signature, proxy);
    }

}
