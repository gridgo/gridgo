package io.gridgo.connector.impl;

import io.gridgo.connector.HasResponder;
import io.gridgo.connector.Responder;
import io.gridgo.connector.support.config.ConnectorContext;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractHasResponderConsumer extends AbstractConsumer implements HasResponder {

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Responder responder;

    public AbstractHasResponderConsumer(ConnectorContext context) {
        super(context);
    }
}
