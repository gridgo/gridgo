package io.gridgo.framework.execution.impl;

import io.gridgo.framework.execution.ExecutionStrategy;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.context.ExecutionContext;
import lombok.NonNull;

public class DefaultExecutionStrategy implements ExecutionStrategy {

    public void execute(final @NonNull Runnable runnable, Message request) {
        runnable.run();
    }

    @Override
    public void execute(ExecutionContext<Message, Message> context) {
        context.execute();
    }
}
