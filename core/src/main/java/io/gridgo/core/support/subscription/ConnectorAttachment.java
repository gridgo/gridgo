package io.gridgo.core.support.subscription;

import io.gridgo.connector.Connector;
import io.gridgo.core.support.transformers.MessageTransformer;

/**
 * Represents a connector attachment. This class is used for a "fluent" API,
 * so that you can modify the transformer of the connector after attaching it to
 * a gateway.
 */
public interface ConnectorAttachment {

    /**
     * Attach a MessageTransformer to this connector when transform incoming
     * messages.
     *
     * @param transformer the transformer
     * @return this ConnectorSubscription
     */
    public ConnectorAttachment transformIncomingWith(MessageTransformer transformer);

    /**
     * Attach a MessageTransformer to this connector when transform outgoing
     * messages.
     *
     * @param transformer the transformer
     * @return this ConnectorSubscription
     */
    public ConnectorAttachment transformOutgoingWith(MessageTransformer transformer);

    /**
     * Finish subscribing this connector.
     *
     * @return the GatewaySubscription which this connector is attached
     */
    public GatewaySubscription finishAttaching();

    public MessageTransformer getIncomingTransformer();

    public MessageTransformer getOutgoingTransformer();

    public Connector getConnector();
}
