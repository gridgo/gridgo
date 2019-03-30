package io.gridgo.boot.support.injectors.impl;

import io.gridgo.boot.support.Injector;
import io.gridgo.boot.support.annotations.AnnotationUtils;
import io.gridgo.boot.support.annotations.RegistryInject;
import io.gridgo.core.GridgoContext;
import io.gridgo.utils.ObjectUtils;

public class RegistryInjector implements Injector {
    
    private GridgoContext context;

    public RegistryInjector(GridgoContext context) {
        this.context = context;
    }

    @Override
    public void inject(Class<?> gatewayClass, Object instance) {
        var fields = AnnotationUtils.findAllFieldsWithAnnotation(gatewayClass, RegistryInject.class);
        for (var field : fields) {
            var name = field.getName();
            var injectedKey = field.getAnnotation(RegistryInject.class).value();
            var injectedValue = context.getRegistry().lookup(injectedKey, field.getType());
            ObjectUtils.setValue(instance, name, injectedValue);
        }
    }
}
