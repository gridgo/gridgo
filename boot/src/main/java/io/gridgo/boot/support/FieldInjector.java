package io.gridgo.boot.support;

import io.gridgo.boot.support.annotations.AnnotationUtils;
import io.gridgo.boot.support.annotations.GatewayInject;
import io.gridgo.boot.support.annotations.RegistryInject;
import io.gridgo.core.GridgoContext;
import io.gridgo.utils.ObjectUtils;

public class FieldInjector {
    
    private GridgoContext context;

    public FieldInjector(GridgoContext context) {
        this.context = context;
    }

    public void injectFields(Class<?> gatewayClass, Object instance) {
        injectRegistries(gatewayClass, instance);
        injectGateways(gatewayClass, instance);
    }

    private void injectRegistries(Class<?> gatewayClass, Object instance) {
        var fields = AnnotationUtils.findAllFieldsWithAnnotation(gatewayClass, RegistryInject.class);
        for (var field : fields) {
            var name = field.getName();
            var injectedKey = field.getAnnotation(RegistryInject.class).value();
            var injectedValue = context.getRegistry().lookup(injectedKey, field.getType());
            ObjectUtils.setValue(instance, name, injectedValue);
        }
    }

    private void injectGateways(Class<?> gatewayClass, Object instance) {
        var fields = AnnotationUtils.findAllFieldsWithAnnotation(gatewayClass, GatewayInject.class);
        for (var field : fields) {
            var name = field.getName();
            var injectedKey = field.getAnnotation(GatewayInject.class).value();
            var gateway = context.findGatewayMandatory(injectedKey);
            ObjectUtils.setValue(instance, name, gateway);
        }
    }
}
