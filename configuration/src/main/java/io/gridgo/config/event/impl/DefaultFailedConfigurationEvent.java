package io.gridgo.config.event.impl;

import io.gridgo.config.event.FailedConfigurationEvent;
import lombok.Getter;

@Getter
public class DefaultFailedConfigurationEvent implements FailedConfigurationEvent {

    private Object source;

    private Throwable cause;

    public DefaultFailedConfigurationEvent(Throwable cause, Object source) {
        this.cause = cause;
        this.source = source;
    }
}
