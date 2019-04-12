package io.gridgo.framework.execution.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.gridgo.framework.execution.ExecutionStrategy;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.context.ExecutionContext;
import lombok.NonNull;

public class ExecutorExecutionStrategy implements ExecutionStrategy {

    private final boolean ownedExecutor;

    private final ExecutorService executor;

    public ExecutorExecutionStrategy(final int noThreads) {
        this.executor = Executors.newFixedThreadPool(noThreads);
        this.ownedExecutor = true;
    }

    public ExecutorExecutionStrategy(final @NonNull ExecutorService executor) {
        this.executor = executor;
        this.ownedExecutor = false;
    }

    @Override
    public void execute(final @NonNull Runnable runnable, Message request) {
        executor.submit(runnable);
    }

    @Override
    public void execute(ExecutionContext<Message, Message> context) {
        executor.submit(context::execute);
    }

    @Override
    public void start() {
        // Nothing to do here
    }

    @Override
    public void stop() {
        if (ownedExecutor)
            executor.shutdown();
    }
}
