package io.gridgo.core.impl;

import java.util.Optional;

import io.gridgo.bean.BValue;
import io.gridgo.connector.impl.AbstractMessageComponent;
import io.gridgo.core.Gateway;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.Processor;
import io.gridgo.core.support.ContextAwareComponent;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractProcessor extends AbstractMessageComponent implements Processor, ContextAwareComponent {

    @Getter
    @Setter
    private GridgoContext context;

    public Gateway withGateway(String name) {
        return context.findGatewayMandatory(name);
    }

    @Override
    public String generateName() {
        return getClass().getName();
    }

    @Override
    protected Optional<BValue> generateId() {
        return Optional.empty();
    }

    @Override
    protected void onStart() {
        // Nothing to do here
    }

    @Override
    protected void onStop() {
        // Nothing to do here
    }
}