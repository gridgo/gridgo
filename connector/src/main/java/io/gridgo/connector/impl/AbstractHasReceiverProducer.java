package io.gridgo.connector.impl;

import io.gridgo.connector.HasReceiver;
import io.gridgo.connector.Receiver;
import io.gridgo.connector.support.config.ConnectorContext;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractHasReceiverProducer extends AbstractProducer implements HasReceiver {

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Receiver receiver;

    protected AbstractHasReceiverProducer(ConnectorContext context) {
        super(context);
    }
}
