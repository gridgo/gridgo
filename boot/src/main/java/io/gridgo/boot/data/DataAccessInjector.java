package io.gridgo.boot.data;

import static io.gridgo.boot.data.GridgoDataConstants.PACKAGE_REGISTRY;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.reflections.Reflections;

import io.gridgo.boot.data.exceptions.SchemaNoHandlerException;
import io.gridgo.boot.data.support.annotations.DataAccess;
import io.gridgo.boot.data.support.annotations.DataAccessInject;
import io.gridgo.boot.data.support.annotations.DataAccessSchema;
import io.gridgo.boot.support.Injector;
import io.gridgo.boot.support.annotations.AnnotationUtils;
import io.gridgo.boot.support.exceptions.InitializationException;
import io.gridgo.core.GridgoContext;
import io.gridgo.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataAccessInjector implements Injector {

    private final Map<String, Class<? extends DataAccessHandler>> handlerMap;

    private static final String[] DEFAULT_PACKAGE = new String[] { DataAccessInjector.class.getPackageName() };

    private GridgoContext context;

    public DataAccessInjector(GridgoContext context) {
        this.context = context;
        this.handlerMap = new HashMap<>();
        loadAllHandlers();
    }

    private void loadAllHandlers() {
        var packages = DEFAULT_PACKAGE;
        var pkgNames = context.getRegistry().lookup(PACKAGE_REGISTRY, String.class);
        if (pkgNames != null)
            packages = pkgNames.split(",");
        log.trace("Scanning packages {} for handlers", String.join(",", packages));
        var ref = new Reflections((Object[]) packages);
        var types = ref.getSubTypesOf(DataAccessHandler.class);
        for (var type : types) {
            registerType(type);
        }
    }

    private void registerType(Class<? extends DataAccessHandler> type) {
        var annotation = type.getAnnotation(DataAccessSchema.class);
        if (annotation == null) {
            log.warn("Class {} is not annotated with @DataAccessSchema", type.getName());
            return;
        }
        var schemas = annotation.value().split(",");
        for (var schema : schemas) {
            if (handlerMap.containsKey(schema)) {
                log.warn("Schema {} is already registered with a handler of type {}. It will be overriden", schema,
                        handlerMap.get(schema).getName());
            }
            handlerMap.put(schema, type);
        }
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
        var schema = annotation.schema();
        if (!handlerMap.containsKey(schema)) {
            throw new SchemaNoHandlerException("No handler found for schema " + schema);
        }
        var proxy = initDataAccessProxy(targetGateway, type, handlerMap.get(schema));
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
