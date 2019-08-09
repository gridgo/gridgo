package io.gridgo.utils.pojo.getter;

import io.gridgo.utils.pojo.AbstractProxyRegistry;
import io.gridgo.utils.pojo.PojoMethodSignature;
import lombok.Getter;

class PojoGetterRegistryImpl extends AbstractProxyRegistry<PojoGetterProxy> implements PojoGetterRegistry {

    @Getter
    private static final PojoGetterRegistry instance = new PojoGetterRegistryImpl();

    private final PojoGetterProxyBuilder getterProxyBuilder = PojoGetterProxyBuilder.newJavassist();

    private PojoGetterRegistryImpl() {
    }

    @Override
    public PojoGetterProxy getGetterProxy(Class<?> type) {
        return this.getProxy(type);
    }

    @Override
    protected PojoGetterProxy buildMandatory(Class<?> type) {
        return getterProxyBuilder.buildGetterProxy(type);
    }

    @Override
    protected void setProxy(PojoMethodSignature signature, PojoGetterProxy proxy) {
        setGetterProxy(signature, proxy);
    }

    @Override
    protected void setElementProxy(PojoMethodSignature signature, PojoGetterProxy proxy) {
        setElementGetterProxy(signature, proxy);
    }

}
