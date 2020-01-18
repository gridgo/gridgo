package io.gridgo.core.support;

import io.gridgo.core.GridgoContext;
import io.gridgo.framework.support.Message;

/**
 * Represents a lifecycle event
 */
public interface LifecycleEvent {

    /**
     * Get the event type
     *
     * @return the event type
     */
    public LifecycleType getType();

    /**
     * Get the message associated with this event
     *
     * @return the message
     */
    public Message getMessage();

    /**
     * Get the gridgo context
     *
     * @return the gridgo context
     */
    public GridgoContext getContext();
}
