package io.gridgo.connector;

import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.framework.support.RegistryAware;
import lombok.NonNull;

/**
 * Represents a connector factory. Connector factories are used as an
 * abstraction layer to create connectors from endpoints. It usually delegates
 * the job to a <code>ConnectorResolver</code>
 */
public interface ConnectorFactory extends RegistryAware {

    /**
     * Create a connector from an endpoint.
     * 
     * @param endpoint the endpoint
     * @return the connector
     */
    public Connector createConnector(final @NonNull String endpoint);

    /**
     * Create a connector from an endpoint and a context
     * 
     * @param endpoint the endpoint
     * @param context  the context
     * @return the connector
     */
    public Connector createConnector(final @NonNull String endpoint, final @NonNull ConnectorContext context);

    /**
     * Create a connector from an endpoint using a custom resolver.
     * 
     * @param endpoint the endpoint
     * @param resolver the resolver
     * @return the connector
     */
    public Connector createConnector(final @NonNull String endpoint, final @NonNull ConnectorResolver resolver);

    /**
     * Create a connector from an endpoint and a context using a custom resolver.
     * 
     * @param endpoint the endpoint
     * @param resolver the resolver
     * @param context  the context
     * @return the connector
     */
    public Connector createConnector(final @NonNull String endpoint, final @NonNull ConnectorResolver resolver, final @NonNull ConnectorContext context);
}
