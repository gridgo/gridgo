package io.gridgo.boot.data.support;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

import io.gridgo.boot.data.support.annotations.DataAccess;
import io.gridgo.boot.data.support.annotations.DataAccessInject;
import io.gridgo.boot.support.Injector;
import io.gridgo.boot.support.annotations.AnnotationUtils;
import io.gridgo.boot.support.exceptions.InitializationException;
import io.gridgo.core.GridgoContext;
import io.gridgo.utils.ObjectUtils;

public class DataAccessInjector implements Injector {

    private GridgoContext context;

    public DataAccessInjector(GridgoContext context) {
        this.context = context;
    }

    @Override
    public void inject(Class<?> gatewayClass, Object instance) {
        var fields = AnnotationUtils.findAllFieldsWithAnnotation(gatewayClass, DataAccessInject.class);
        for (var field : fields) {
            injectField(gatewayClass, instance, field);
        }
    }

    private void injectField(Class<?> clazz, Object instance, Field field) {
        var type = field.getType();
        var annotation = type.getAnnotation(DataAccess.class);
        if (annotation == null) {
            throw new IllegalArgumentException(String.format("Field %s.%s of type %s is not annotated with @DataAccess",
                    clazz.getName(), field.getName(), field.getType().getName()));
        }
        var targetGateway = annotation.gateway();
        var handler = annotation.handler();
        var proxy = initDataAccessProxy(targetGateway, type, handler);
        ObjectUtils.setValue(instance, field.getName(), proxy);
    }

    private Object initDataAccessProxy(String targetGateway, Class<?> proxyClass,
            Class<? extends DataAccessHandler> handler) {
        try {
            var instance = handler.getConstructor().newInstance();
            instance.setContext(context);
            instance.setGateway(context.findGatewayMandatory(targetGateway));
            return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { proxyClass },
                    instance);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new InitializationException("Cannot inject data access layer", e);
        }
    }
}
