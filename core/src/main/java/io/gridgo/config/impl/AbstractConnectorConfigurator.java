package io.gridgo.config.impl;

import io.gridgo.connector.Connector;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class AbstractConnectorConfigurator extends AbstractConfigurator {

    @Getter(value = AccessLevel.PROTECTED)
    private Connector connector;

    private boolean owned;

    protected AbstractConnectorConfigurator(Connector connector, boolean owned) {
        this.connector = connector;
        this.owned = owned;
    }

    @Override
    protected void onStart() {
        if (owned)
            connector.start();
    }

    @Override
    protected void onStop() {
        if (owned)
            connector.stop();
    }
}
