package io.gridgo.core.impl;

import java.util.Optional;

import io.gridgo.bean.BValue;
import io.gridgo.connector.impl.AbstractMessageComponent;
import io.gridgo.core.Processor;

public abstract class AbstractProcessor extends AbstractMessageComponent implements Processor {
    
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