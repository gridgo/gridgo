package io.gridgo.config;

import java.util.Optional;

import io.gridgo.bean.BElement;
import io.gridgo.config.event.ConfigurationEvent;
import io.gridgo.framework.ComponentLifecycle;
import io.gridgo.framework.EventDispatcher;

public interface Configurator extends ComponentLifecycle, EventDispatcher<ConfigurationEvent> {

    public Optional<BElement> get();
}
