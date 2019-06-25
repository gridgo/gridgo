package io.gridgo.config.event.impl;

import io.gridgo.bean.BElement;
import io.gridgo.config.event.LoadedConfigurationEvent;
import lombok.Getter;

@Getter
public class DefaultLoadedConfigurationEvent implements LoadedConfigurationEvent {

    private Object source;

    private BElement configObject;

    public DefaultLoadedConfigurationEvent(BElement configObject, Object source) {
        this.configObject = configObject;
        this.source = source;
    }
}
