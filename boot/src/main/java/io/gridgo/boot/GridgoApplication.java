package io.gridgo.boot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.reflections.Reflections;

import io.gridgo.boot.config.ResourceConfigurator;
import io.gridgo.boot.registry.AnnotatedRegistry;
import io.gridgo.boot.support.annotations.AnnotationUtils;
import io.gridgo.boot.support.annotations.EnableComponentScan;
import io.gridgo.boot.support.annotations.Gateway;
import io.gridgo.boot.support.annotations.RegistryInitializer;
import io.gridgo.boot.support.exceptions.InitializationException;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.Processor;
import io.gridgo.core.impl.ConfiguratorContextBuilder;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.framework.impl.AbstractComponentLifecycle;
import io.gridgo.framework.support.Registry;
import io.gridgo.utils.ThreadUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class GridgoApplication extends AbstractComponentLifecycle {

    private GridgoContext context;

    private String appName;

    private Registry registry;

    private Class<?> applicationClass;

    private GridgoApplication(Class<?> applicationClass) {
        this.applicationClass = applicationClass;
        this.registry = new AnnotatedRegistry(applicationClass);
        this.initializeRegistry();
        var configurator = new ResourceConfigurator();
        this.context = new ConfiguratorContextBuilder().setRegistry(registry) //
                                                       .setConfigurator(configurator) //
                                                       .build();
        this.appName = this.context.getName();
        this.initialize();
    }

    private void initialize() {
        var enableComponentScan = applicationClass.getAnnotation(EnableComponentScan.class);
        if (enableComponentScan != null) {
            scanComponents();
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
        var gateway = context.openGateway(annotation.name());
        for (String connector : annotation.connectors()) {
            gateway.attachConnector(registry.substituteRegistriesRecursive(connector));
        }
        try {
            var instance = gatewayClass.getConstructor().newInstance();
            if (instance instanceof Processor) {
                gateway.subscribe((Processor) instance);
            } else {
                subscribeProcessorMethods(gatewayClass, gateway, instance);
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new InitializationException("Cannot register processor", e);
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

    public static GridgoApplication run(Class<?> applicationClass) {
        var app = new GridgoApplication(applicationClass);
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
}
