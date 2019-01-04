package io.gridgo.config;

import java.util.Optional;

import io.gridgo.bean.BElement;
import io.gridgo.config.event.ConfigurationEvent;
import io.gridgo.core.EventDispatcher;
import io.gridgo.framework.ComponentLifecycle;

public interface Configurator extends ComponentLifecycle, EventDispatcher<ConfigurationEvent> {

    public Optional<BElement> get();
}
