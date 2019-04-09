package io.gridgo.config.impl;

import java.util.Collections;
import java.util.Optional;

import io.gridgo.bean.BElement;
import io.gridgo.config.Configurator;
import io.gridgo.config.event.ConfigurationEvent;
import io.gridgo.config.event.impl.DefaultFailedConfigurationEvent;
import io.gridgo.config.event.impl.DefaultLoadedConfigurationEvent;
import io.gridgo.config.event.impl.DefaultReloadedConfigurationEvent;
import io.gridgo.framework.impl.ReplayEventDispatcher;

public abstract class AbstractConfigurator extends ReplayEventDispatcher<ConfigurationEvent> implements Configurator {

    private Optional<BElement> configObject = Optional.empty();

    @Override
    public Optional<BElement> get() {
        return configObject;
    }

    protected void publishLoaded(BElement result) {
        configObject = Optional.ofNullable(result);
        publish(new DefaultLoadedConfigurationEvent(result, this));
    }

    protected void publishReloaded(BElement result) {
        configObject = Optional.ofNullable(result);
        publish(new DefaultReloadedConfigurationEvent(result, Collections.emptyMap(), this));
    }

    protected void publishFailed(Throwable cause) {
        publish(new DefaultFailedConfigurationEvent(cause, this));
    }
}
