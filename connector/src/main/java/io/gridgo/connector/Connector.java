package io.gridgo.connector;

import java.util.Optional;

import io.gridgo.connector.support.config.ConnectorConfig;
import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.framework.ComponentLifecycle;

/**
 * Represents a connection to an endpoint. And endpoint can be local, remote or
 * even in-process. Each connector will have a producer and consumer. Connector
 * are created using <code>DefaultConnectorFactory</code>
 * 
 */
public interface Connector extends ComponentLifecycle {

    /**
     * Get the connector config with which this Connector is initialized.
     * 
     * @return the connector config
     */
    public ConnectorConfig getConnectorConfig();

    /**
     * Get the associated consumer. Some connectors might not have consumer and thus
     * will return <code>Optional.empty()</code>
     * 
     * @return the consumer
     */
    public Optional<Consumer> getConsumer();

    /**
     * Get the associated producer. Some connectors might not have producer and thus
     * will return <code>Optional.empty()</code>
     * 
     * @return the producer
     */
    public Optional<Producer> getProducer();

    /**
     * Initialize the connector. This is meant to be called from Gridgo only.
     * 
     * @param config  the configuration
     * @param context the context
     * @return the connector itself for easily chaining method calls
     */
    public Connector initialize(ConnectorConfig config, ConnectorContext context);
}
