package io.gridgo.boot;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.reflections.Reflections;

import io.gridgo.boot.config.ResourceConfigurator;
import io.gridgo.boot.registry.AnnotatedRegistry;
import io.gridgo.boot.support.AnnotationScanner;
import io.gridgo.boot.support.FieldInjector;
import io.gridgo.boot.support.LazyInitializer;
import io.gridgo.boot.support.annotations.AnnotationUtils;
import io.gridgo.boot.support.annotations.EnableComponentScan;
import io.gridgo.boot.support.annotations.RegistryInitializer;
import io.gridgo.boot.support.exceptions.InitializationException;
import io.gridgo.boot.support.exceptions.ResourceNotFoundException;
import io.gridgo.boot.support.scanners.impl.GatewayScanner;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.impl.ConfiguratorContextBuilder;
import io.gridgo.core.impl.DefaultGridgoContextBuilder;
import io.gridgo.framework.impl.AbstractComponentLifecycle;
import io.gridgo.framework.support.Registry;
import io.gridgo.utils.ThreadUtils;
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

    private FieldInjector injector;

    private List<AnnotationScanner> scanners;

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
        this.injector = new FieldInjector(context);
        this.lazyInitializers = new ArrayList<>();
        this.scanners = new ArrayList<>();
        this.initialize();
    }

    private void initialize() {
        var enableComponentScan = applicationClass.getAnnotation(EnableComponentScan.class);
        if (enableComponentScan != null) {
            scanComponents();
        }
        for (var initializer : lazyInitializers) {
            injector.injectFields(initializer.getGatewayClass(), initializer.getInstance());
        }
    }

    private void scanComponents() {
        var pkg = applicationClass.getPackageName();
        var ref = new Reflections(pkg);
        this.scanners.add(new GatewayScanner());
        for (var scanner : scanners) {
            scanner.scanAnnotation(ref, context, lazyInitializers);
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
}
