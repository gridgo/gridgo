package io.gridgo.connector.impl;

import io.gridgo.connector.Receiver;
import io.gridgo.connector.support.config.ConnectorContext;

public abstract class AbstractReceiver extends AbstractConsumer implements Receiver {

    protected AbstractReceiver(ConnectorContext context) {
        super(context);
    }

    @Override
    protected void onStart() {
        // do nothing
    }

    @Override
    protected void onStop() {
        // do nothing
    }
}
