package io.gridgo.config.impl;

import java.util.Optional;

import io.gridgo.bean.BElement;
import io.gridgo.config.Configurator;
import io.gridgo.config.event.ConfigurationEvent;
import io.gridgo.config.event.impl.DefaultLoadedConfigurationEvent;
import io.gridgo.core.impl.ReplayEventDispatcher;

public abstract class AbstractLocalConfigurator extends ReplayEventDispatcher<ConfigurationEvent> implements Configurator {

    private Optional<BElement> configObject = Optional.empty();

    @Override
    protected void onStart() {
        this.configObject = resolve();
        publish(new DefaultLoadedConfigurationEvent(configObject.orElse(null), this));
    }

    @Override
    protected void onStop() {

    }

    @Override
    public Optional<BElement> get() {
        return configObject;
    }

    protected abstract Optional<BElement> resolve();
}
