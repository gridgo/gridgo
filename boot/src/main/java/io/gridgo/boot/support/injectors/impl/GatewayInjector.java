package io.gridgo.boot.support.injectors.impl;

import io.gridgo.boot.support.Injector;
import io.gridgo.boot.support.annotations.AnnotationUtils;
import io.gridgo.boot.support.annotations.GatewayInject;
import io.gridgo.core.GridgoContext;
import io.gridgo.utils.ObjectUtils;

public class GatewayInjector implements Injector {

    private GridgoContext context;

    public GatewayInjector(GridgoContext context) {
        this.context = context;
    }

    @Override
    public void inject(Class<?> gatewayClass, Object instance) {
        var fields = AnnotationUtils.findAllFieldsWithAnnotation(gatewayClass, GatewayInject.class);
        for (var field : fields) {
            var name = field.getName();
            var injectedKey = field.getAnnotation(GatewayInject.class).value();
            var gateway = context.findGatewayMandatory(injectedKey);
            ObjectUtils.setValue(instance, name, gateway);
        }
    }
}
