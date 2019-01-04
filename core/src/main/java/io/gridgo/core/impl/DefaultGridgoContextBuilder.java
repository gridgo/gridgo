package io.gridgo.core.impl;

import java.util.function.Consumer;

import io.gridgo.connector.ConnectorFactory;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.GridgoContextBuilder;
import io.gridgo.framework.support.Registry;

public class DefaultGridgoContextBuilder implements GridgoContextBuilder {

    private ConnectorFactory connectorFactory;

    private Registry registry;

    private Consumer<Throwable> exceptionHandler;

    private String name;

    public DefaultGridgoContextBuilder setConnectorFactory(ConnectorFactory connectorFactory) {
        this.connectorFactory = connectorFactory;
        return this;
    }

    public DefaultGridgoContextBuilder setExceptionHandler(Consumer<Throwable> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public DefaultGridgoContextBuilder setRegistry(Registry registry) {
        this.registry = registry;
        return this;
    }

    public DefaultGridgoContextBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public GridgoContext build() {
        if (this.connectorFactory != null)
            this.connectorFactory.setRegistry(registry);
        return new DefaultGridgoContext(name, connectorFactory, registry, exceptionHandler);
    }
}
