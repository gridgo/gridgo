package io.gridgo.core.support.subscription.impl;

import io.gridgo.connector.Connector;
import io.gridgo.core.support.subscription.ConnectorAttachment;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.core.support.transformers.MessageTransformer;
import lombok.Getter;

@Getter
public class DefaultConnectorAttachment implements ConnectorAttachment {

    private GatewaySubscription gateway;

    private Connector connector;

    private MessageTransformer incomingTransformer;

    private MessageTransformer outgoingTransformer;

    public DefaultConnectorAttachment(GatewaySubscription gateway, Connector connector) {
        this.gateway = gateway;
        this.connector = connector;
    }

    @Override
    public ConnectorAttachment transformIncomingWith(MessageTransformer transformer) {
        this.incomingTransformer = transformer;
        return this;
    }

    @Override
    public ConnectorAttachment transformOutgoingWith(MessageTransformer transformer) {
        this.outgoingTransformer = transformer;
        return this;
    }

    @Override
    public GatewaySubscription finishAttaching() {
        return gateway;
    }
}
