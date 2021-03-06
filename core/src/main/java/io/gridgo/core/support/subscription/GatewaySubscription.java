package io.gridgo.core.support.subscription;

import java.util.List;

import io.gridgo.connector.Connector;
import io.gridgo.connector.ConnectorResolver;
import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.core.Gateway;
import io.gridgo.core.Processor;
import io.gridgo.core.support.ProducerTemplateAware;
import io.gridgo.framework.execution.ProducerInstrumenter;

/**
 * Represents a gateway subscription. After a gateway is opened, you can attach
 * connectors, routing policies and subscriber to it. A gateway can be started
 * automatically when the context starts, or it can be lazily started with
 * <code>setAutoStart(false)</code>
 */
public interface GatewaySubscription extends ProducerTemplateAware<GatewaySubscription> {

    /**
     * Attach a connector to the gateway with an endpoint.
     *
     * @param endpoint the endpoint
     * @return the connector attachment
     */
    public ConnectorAttachment attachConnector(String endpoint);

    /**
     * Attach a connector to the gateway with an endpoint using a custom resolver.
     *
     * @param endpoint the endpoint
     * @param resolver the resolver
     * @return the connector attachment
     */
    public ConnectorAttachment attachConnector(String endpoint, ConnectorResolver resolver);

    /**
     * Attach a connector to the gateway with an endpoint and a context.
     *
     * @param endpoint         the endpoint
     * @param connectorContext the context
     * @return the connector attachment
     */
    public ConnectorAttachment attachConnector(String endpoint, ConnectorContext connectorContext);

    /**
     * Attach a connector to the gateway with an endpoint and a context using a
     * custom resolver.
     *
     * @param endpoint         the endpoint
     * @param resolver         the resolver
     * @param connectorContext the context
     * @return the connector attachment
     */
    public ConnectorAttachment attachConnector(String endpoint, ConnectorResolver resolver,
            ConnectorContext connectorContext);

    /**
     * Attach a connector object to the gateway.
     *
     * @param connector the connector
     * @return the connector attachment
     */
    public ConnectorAttachment attachConnector(Connector connector);

    /**
     * Attach a routing policy to the gateway.
     *
     * @param policy the policy
     * @return the GatewaySubscription itself
     */
    public GatewaySubscription attachRoutingPolicy(RoutingPolicy policy);

    /**
     * Subscribe a processor so that it can receive messages from the gateway's
     * attached connectors.
     *
     * @param processor the processor
     * @return the ProcessorSubscription
     */
    public ProcessorSubscription subscribe(Processor processor);

    /**
     * Set the gateway autoStart flag.
     *
     * @param autoStart the autoStart value
     * @return the GatewaySubscription itself
     */
    public GatewaySubscription setAutoStart(boolean autoStart);

    public GatewaySubscription setProducerInstrumenter(ProducerInstrumenter instrumenter);

    /**
     * Get the list of subscriptions attached to this gateway
     *
     * @return the list of subscriptions
     */
    public List<ProcessorSubscription> getSubscriptions();

    /**
     * Get the Gateway associated with this subscription.
     *
     * @return the associated gateway
     */
    public Gateway get();
}
