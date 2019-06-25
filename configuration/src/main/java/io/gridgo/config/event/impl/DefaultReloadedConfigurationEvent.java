package io.gridgo.config.event.impl;

import java.util.Map;

import io.gridgo.bean.BElement;
import io.gridgo.config.event.ConfigurationChanged;
import io.gridgo.config.event.ReloadedConfigurationEvent;
import lombok.Getter;

@Getter
public class DefaultReloadedConfigurationEvent implements ReloadedConfigurationEvent {

    private Object source;

    private BElement configObject;

    private Map<String, ConfigurationChanged> changes;

    public DefaultReloadedConfigurationEvent(BElement configObject, Map<String, ConfigurationChanged> changes,
            Object source) {
        this.configObject = configObject;
        this.changes = changes;
        this.source = source;
    }
}
