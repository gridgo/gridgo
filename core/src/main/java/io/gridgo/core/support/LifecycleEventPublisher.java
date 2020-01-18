package io.gridgo.core.support;

import io.gridgo.bean.BObject;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.impl.DefaultLifecycleEvent;
import io.gridgo.framework.ComponentLifecycle;
import io.gridgo.framework.support.Message;

public interface LifecycleEventPublisher {

    public GridgoContext getContext();

    public default void publish(LifecycleType eventType, ComponentLifecycle component) {
        publish(eventType, Message.ofAny(BObject.of("name", component.getName())));
    }

    public default void publish(LifecycleType eventType, Message eventPayload) {
        eventPayload.headers().putAny("type", eventType.name());
        var context = getContext();
        var event = DefaultLifecycleEvent.builder()
                .type(eventType)
                .message(eventPayload)
                .context(context)
                .build();
        context.publish(event);
    }
}
