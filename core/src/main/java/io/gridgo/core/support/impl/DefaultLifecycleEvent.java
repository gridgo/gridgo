package io.gridgo.core.support.impl;

import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.LifecycleEvent;
import io.gridgo.core.support.LifecycleType;
import io.gridgo.framework.support.Message;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DefaultLifecycleEvent implements LifecycleEvent {

    private LifecycleType type;

    private Message message;

    private GridgoContext context;

    @Override
    public String toString() {
        return "LifecycleEvent(type = " + type + ", message = " + message + ")";
    }
}
