package io.gridgo.core.impl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import io.gridgo.bean.BObject;
import io.gridgo.config.Configurator;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.GridgoContextBuilder;
import io.gridgo.core.support.config.ContextConfiguratorParser;
import io.gridgo.core.support.config.impl.DefaultContextConfigurationParser;
import io.gridgo.core.support.config.impl.DefaultContextConfiguratorVisitor;
import io.gridgo.framework.support.Registry;

public class ConfiguratorContextBuilder implements GridgoContextBuilder {

    private Registry registry;

    private Configurator configurator;

    private ContextConfiguratorParser parser = new DefaultContextConfigurationParser();

    @Override
    public GridgoContext build() {
        var ref = waitForConfig();

        var result = ref.get();
        if (result instanceof RuntimeException)
            throw (RuntimeException) result;
        if (result instanceof Throwable)
            throw new RuntimeException((Throwable) result);

        var visitor = new DefaultContextConfiguratorVisitor();
        visitor.setRegistry(registry);
        return visitor.visit(parser.parse((BObject) result));
    }

    private AtomicReference<Object> waitForConfig() {
        var ref = new AtomicReference<>();
        var latch = new CountDownLatch(1);
        var disposable = configurator.subscribe(event -> {
            if (event.isLoaded()) {
                ref.compareAndSet(null, event.asLoaded().getConfigObject());
            } else if (event.isReloaded()) {
                ref.compareAndSet(null, event.asReloaded().getConfigObject());
            } else if (event.isFailed()) {
                ref.compareAndSet(null, event.asFailed().getCause());
            }
            latch.countDown();
        });
        configurator.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        disposable.dispose();
        return ref;
    }

    public ConfiguratorContextBuilder setRegistry(Registry registry) {
        this.registry = registry;
        return this;
    }

    public ConfiguratorContextBuilder setConfigurator(Configurator configurator) {
        this.configurator = configurator;
        return this;
    }

    public ConfiguratorContextBuilder setContextConfiguratorParser(ContextConfiguratorParser parser) {
        this.parser = parser;
        return this;
    }
}
