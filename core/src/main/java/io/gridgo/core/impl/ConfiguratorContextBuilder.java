package io.gridgo.core.impl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import io.gridgo.bean.BObject;
import io.gridgo.config.Configurator;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.ContextConfiguratorParser;
import io.gridgo.core.support.GridgoContextBuilder;
import io.gridgo.core.support.impl.DefaultContextConfigurationParser;
import io.gridgo.framework.support.Registry;

public class ConfiguratorContextBuilder implements GridgoContextBuilder {
    
    private Registry registry;

    private Configurator configurator;

    private ContextConfiguratorParser parser = new DefaultContextConfigurationParser();

    @Override
    public GridgoContext build() {
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
        var result = ref.get();
        if (result instanceof Throwable)
            throw new RuntimeException((Throwable) result);
        return parser.parse(registry, (BObject) result);
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
