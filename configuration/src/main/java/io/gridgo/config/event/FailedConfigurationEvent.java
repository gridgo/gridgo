package io.gridgo.config.event;

public interface FailedConfigurationEvent extends ConfigurationEvent {

    public default ConfigurationEventType getEventType() {
        return ConfigurationEventType.FAILED;
    }
    
    public Throwable getCause();
}
