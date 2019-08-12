package io.gridgo.connector.impl.factories;

import io.gridgo.connector.Connector;
import io.gridgo.connector.ConnectorFactory;
import io.gridgo.connector.ConnectorResolver;
import io.gridgo.connector.impl.resolvers.ClasspathConnectorResolver;
import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.connector.support.config.ConnectorContextBuilder;
import io.gridgo.connector.support.config.impl.DefaultConnectorContextBuilder;
import io.gridgo.framework.support.Registry;
import lombok.Getter;
import lombok.NonNull;

public class DefaultConnectorFactory implements ConnectorFactory {

    public static final ConnectorResolver DEFAULT_CONNECTOR_RESOLVER = new ClasspathConnectorResolver();

    private final ConnectorResolver resolver;

    private final ConnectorContextBuilder builder;

    @Getter
    private Registry registry;

    private boolean ownedBuilder = true;

    public DefaultConnectorFactory() {
        this.resolver = DEFAULT_CONNECTOR_RESOLVER;
        this.builder = new DefaultConnectorContextBuilder();
    }

    public DefaultConnectorFactory(final @NonNull ConnectorResolver resolver) {
        this.resolver = resolver;
        this.builder = new DefaultConnectorContextBuilder();
    }

    public DefaultConnectorFactory(final @NonNull ConnectorResolver resolver, ConnectorContextBuilder builder) {
        this.resolver = resolver;
        this.builder = builder;
        ownedBuilder = false;
    }

    @Override
    public Connector createConnector(String endpoint) {
        return createConnector(endpoint, resolver);
    }

    @Override
    public Connector createConnector(String endpoint, ConnectorContext context) {
        return createConnector(endpoint, resolver, context);
    }

    @Override
    public Connector createConnector(String endpoint, ConnectorResolver resolver) {
        return createConnector(endpoint, resolver, builder.build());
    }

    @Override
    public Connector createConnector(String endpoint, ConnectorResolver resolver, ConnectorContext context) {
        if (context.getRegistry() == null)
            context.setRegistry(registry);
        return resolver.resolve(endpoint, context);
    }

    @Override
    public void setRegistry(Registry registry) {
        this.registry = registry;
        if (ownedBuilder)
            builder.setRegistry(registry);
    }
}
