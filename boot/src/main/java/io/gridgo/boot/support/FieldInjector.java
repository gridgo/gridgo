package io.gridgo.boot.support;

import java.util.Arrays;
import java.util.List;

import io.gridgo.boot.data.DataAccessInjector;
import io.gridgo.boot.support.injectors.impl.GatewayInjector;
import io.gridgo.boot.support.injectors.impl.RegistryInjector;
import io.gridgo.core.GridgoContext;

public class FieldInjector {

    private List<Injector> injectors;

    public FieldInjector(GridgoContext context) {
        this.injectors = Arrays.asList( //
                new RegistryInjector(context), //
                new GatewayInjector(context), //
                new DataAccessInjector(context));
    }

    public void injectFields(Class<?> gatewayClass, Object instance) {
        for (var injector : injectors) {
            injector.inject(gatewayClass, instance);
        }
    }
}
