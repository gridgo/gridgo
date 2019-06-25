package io.gridgo.connector;

import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.connector.support.config.impl.DefaultConnectorContext;
import lombok.NonNull;

/**
 * Represents a connector resolver. Resolver is used to create
 * <code>Connector</code> from an endpoint.
 */
public interface ConnectorResolver {

    /**
     * Resolve a connector using an endpoint.
     * 
     * @param endpoint the endpoint
     * @return the resolved connector
     */
    public default Connector resolve(final @NonNull String endpoint) {
        return resolve(endpoint, new DefaultConnectorContext());
    }

    /**
     * Resolve a connector using an endpoint and an optional
     * <code>ConnectorContext</code>.
     * 
     * @param endpoint         the endpoint
     * @param connectorContext the context
     * @return the resolved connector
     */
    public Connector resolve(final @NonNull String endpoint, ConnectorContext connectorContext);
}
