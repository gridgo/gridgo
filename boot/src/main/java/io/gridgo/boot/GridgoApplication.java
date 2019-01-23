package io.gridgo.boot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.reflections.Reflections;

import io.gridgo.boot.config.ResourceConfigurator;
import io.gridgo.boot.registry.AnnotatedRegistry;
import io.gridgo.boot.support.annotations.AnnotationUtils;
import io.gridgo.boot.support.annotations.Connector;
import io.gridgo.boot.support.annotations.EnableComponentScan;
import io.gridgo.boot.support.annotations.Gateway;
import io.gridgo.boot.support.annotations.GatewayInject;
import io.gridgo.boot.support.annotations.RegistryInitializer;
import io.gridgo.boot.support.annotations.RegistryInject;
import io.gridgo.boot.support.exceptions.InitializationException;
import io.gridgo.boot.support.exceptions.ResourceNotFoundException;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.Processor;
import io.gridgo.core.impl.ConfiguratorContextBuilder;
import io.gridgo.core.impl.DefaultGridgoContextBuilder;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.framework.impl.AbstractComponentLifecycle;
import io.gridgo.framework.support.Registry;
import io.gridgo.utils.ObjectUtils;
import io.gridgo.utils.ThreadUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class GridgoApplication extends AbstractComponentLifecycle {

    private GridgoContext context;

    private String appName;

    private Registry registry;

    private Class<?> applicationClass;

    private String[] args;

    private List<LazyInitializer> lazyInitializers;

    private GridgoApplication(Class<?> applicationClass, String... args) {
        this.applicationClass = applicationClass;
        this.args = args;
        this.registry = new AnnotatedRegistry(applicationClass);
        this.initializeRegistry();
        try {
            var configurator = new ResourceConfigurator();
            this.context = new ConfiguratorContextBuilder().setRegistry(registry) //
                                                           .setConfigurator(configurator) //
                                                           .build();
        } catch (ResourceNotFoundException ex) {
            this.context = new DefaultGridgoContextBuilder().setRegistry(registry).build();
        }
        this.appName = this.context.getName();
        this.lazyInitializers = new ArrayList<LazyInitializer>();
        this.initialize();
    }

    private void initialize() {
        var enableComponentScan = applicationClass.getAnnotation(EnableComponentScan.class);
        if (enableComponentScan != null) {
            scanComponents();
        }
        for (var initializer : lazyInitializers) {
            injectFields(initializer.gatewayClass, initializer.instance);
        }
    }

    private void scanComponents() {
        var pkg = applicationClass.getPackageName();
        var ref = new Reflections(pkg);
        scanGateways(ref);
    }

    private void scanGateways(Reflections ref) {
        var gateways = ref.getTypesAnnotatedWith(Gateway.class);
        for (var gateway : gateways) {
            registerGateway(gateway);
        }
    }

    private void registerGateway(Class<?> gatewayClass) {
        var annotation = gatewayClass.getAnnotation(io.gridgo.boot.support.annotations.Gateway.class);
        var gateway = context.openGateway(annotation.value());
        var connectors = gatewayClass.getAnnotationsByType(Connector.class);
        for (var connector : connectors) {
            gateway.attachConnector(registry.substituteRegistriesRecursive(connector.value()));
        }
        try {
            var instance = gatewayClass.getConstructor().newInstance();
            if (instance instanceof Processor) {
                gateway.subscribe((Processor) instance);
            } else {
                subscribeProcessorMethods(gatewayClass, gateway, instance);
            }
            lazyInitializers.add(new LazyInitializer(gatewayClass, instance));
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new InitializationException("Cannot register processor", e);
        }
    }

    private void injectFields(Class<?> gatewayClass, Object instance) {
        injectRegistries(gatewayClass, instance);
        injectGateways(gatewayClass, instance);
    }

    private void injectRegistries(Class<?> gatewayClass, Object instance) {
        var fields = AnnotationUtils.findAllFieldsWithAnnotation(gatewayClass, RegistryInject.class);
        for (var field : fields) {
            var name = field.getName();
            var injectedKey = field.getAnnotation(RegistryInject.class).value();
            var injectedValue = registry.lookup(injectedKey, field.getType());
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

    private void subscribeProcessorMethods(Class<?> gatewayClass, GatewaySubscription gateway, Object instance) {
        var methods = AnnotationUtils.findAllMethodsWithAnnotation(gatewayClass,
                io.gridgo.boot.support.annotations.Processor.class);
        for (var method : methods) {
            var staticMethod = Modifier.isStatic(method.getModifiers());
            gateway.subscribe((rc, gc) -> {
                try {
                    if (staticMethod)
                        method.invoke(null, rc, gc);
                    else
                        method.invoke(instance, rc, gc);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    rc.getDeferred().reject(e);
                }
            });
        }
    }

    public static GridgoApplication run(Class<?> applicationClass, String... args) {
        var app = new GridgoApplication(applicationClass, args);
        ThreadUtils.registerShutdownTask(app::stop);

        try {
            app.start();
        } catch (Exception ex) {
            log.error("Application start failure", ex);
            app.stop();
        }
        return app;
    }

    private void initializeRegistry() {
        var methods = AnnotationUtils.findAllMethodsWithAnnotation(applicationClass, RegistryInitializer.class);
        for (var method : methods) {
            try {
                var params = method.getParameterCount();
                if (params == 0)
                    method.invoke(null);
                else
                    method.invoke(null, registry);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new InitializationException("Cannot initialize application", e);
            }
        }
    }

    @Override
    protected void onStart() {
        context.start();
    }

    @Override
    protected void onStop() {
        context.stop();
    }

    @Override
    protected String generateName() {
        return "app." + appName;
    }

    @AllArgsConstructor
    class LazyInitializer {

        Class<?> gatewayClass;

        Object instance;
    }
}
