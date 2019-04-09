package io.gridgo.connector.impl;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.connector.Producer;
import io.gridgo.connector.ProducerAck;
import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.framework.impl.AbstractComponentLifecycle;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.impl.DefaultPayload;
import lombok.Getter;
import lombok.NonNull;

public abstract class AbstractProducer extends AbstractComponentLifecycle implements Producer, ProducerAck {

    @Getter
    private final ConnectorContext context;

    protected AbstractProducer(final @NonNull ConnectorContext context) {
        this.context = context;
    }

    protected Message createMessage(BObject headers, BElement body) {
        return Message.of(new DefaultPayload(context.getIdGenerator().generateId(), headers, body));
    }
}
