package io.gridgo.config.event;

import java.util.Map;

import io.gridgo.bean.BElement;

public interface ReloadedConfigurationEvent extends ConfigurationEvent {

    public default ConfigurationEventType getEventType() {
        return ConfigurationEventType.RELOADED;
    }
    
    public BElement getConfigObject();
    
    public Map<String, ConfigurationChanged> getChanges();
}
