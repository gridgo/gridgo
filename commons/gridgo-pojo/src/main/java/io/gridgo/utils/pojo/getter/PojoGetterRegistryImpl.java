package io.gridgo.utils.pojo.getter;

import io.gridgo.utils.pojo.AbstractProxyRegistry;
import io.gridgo.utils.pojo.PojoMethodSignature;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class PojoGetterRegistryImpl extends AbstractProxyRegistry<PojoGetterProxy> implements PojoGetterRegistry {

    @Getter
    private static final PojoGetterRegistry instance = new PojoGetterRegistryImpl();

    private final PojoGetterProxyBuilder getterProxyBuilder = PojoGetterProxyBuilder.newJanino();

    @Override
    public PojoGetterProxy getGetterProxy(Class<?> type) {
        return this.getProxy(type);
    }

    @Override
    protected PojoGetterProxy buildMandatory(Class<?> type) {
        return getterProxyBuilder.buildGetterProxy(type);
    }

    @Override
    protected void setFieldProxy(PojoMethodSignature signature, PojoGetterProxy proxy) {
        injectGetterProxy(signature, proxy);
    }

    @Override
    protected void setFieldElementProxy(PojoMethodSignature signature, PojoGetterProxy proxy) {
        injectElementGetterProxy(signature, proxy);
    }
}
