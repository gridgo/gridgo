package io.gridgo.config.impl;

import java.util.Optional;

import io.gridgo.bean.BElement;

public abstract class AbstractLocalConfigurator extends AbstractConfigurator {

    @Override
    protected void onStart() {
        publishLoaded(resolve().orElse(null));
    }

    @Override
    protected void onStop() {

    }

    protected abstract Optional<BElement> resolve();
}
