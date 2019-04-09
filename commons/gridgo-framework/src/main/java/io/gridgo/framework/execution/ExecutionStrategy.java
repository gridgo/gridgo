package io.gridgo.framework.execution;

import io.gridgo.framework.ComponentLifecycle;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.context.ExecutionContext;
import lombok.NonNull;

/**
 * Represents an execution strategy.
 *
 */
public interface ExecutionStrategy extends ComponentLifecycle {

    public default void execute(final @NonNull Runnable runnable) {
        execute(runnable, null);
    }

    public void execute(final @NonNull Runnable runnable, Message request);

    public void execute(final @NonNull ExecutionContext<Message, Message> context);

    public default String getName() {
        return null;
    }
}
