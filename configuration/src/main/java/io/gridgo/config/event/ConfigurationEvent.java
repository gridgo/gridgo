package io.gridgo.config.event;

public interface ConfigurationEvent {

    public ConfigurationEventType getEventType();

    public Object getSource();

    public default LoadedConfigurationEvent asLoaded() {
        return (LoadedConfigurationEvent) this;
    }

    public default ReloadedConfigurationEvent asReloaded() {
        return (ReloadedConfigurationEvent) this;
    }

    public default FailedConfigurationEvent asFailed() {
        return (FailedConfigurationEvent) this;
    }

    public default boolean isLoaded() {
        return getEventType() == ConfigurationEventType.LOADED;
    }

    public default boolean isReloaded() {
        return getEventType() == ConfigurationEventType.RELOADED;
    }

    public default boolean isFailed() {
        return getEventType() == ConfigurationEventType.FAILED;
    }
}
