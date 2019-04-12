package io.gridgo.config.event;

import io.gridgo.bean.BElement;

public interface LoadedConfigurationEvent extends ConfigurationEvent {

    public default ConfigurationEventType getEventType() {
        return ConfigurationEventType.LOADED;
    }
    
    public BElement getConfigObject();
}
