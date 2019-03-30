package io.gridgo.boot.support;

public interface Injector {

    public void inject(Class<?> gatewayClass, Object instance);
}
