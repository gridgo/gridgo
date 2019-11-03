package io.gridgo.framework.execution.impl;

import java.util.function.Function;

import io.gridgo.framework.execution.ExecutionStrategy;

public abstract class AbstractMultiExecutionStrategy implements ExecutionStrategy {

    protected final int noThreads;

    protected final Function<Integer, ExecutionStrategy> executorSupplier;

    protected ExecutionStrategy[] executors;

    public AbstractMultiExecutionStrategy(final int noThreads, final Function<Integer, ExecutionStrategy> executorSupplier) {
        this.noThreads = noThreads;
        this.executorSupplier = executorSupplier;
    }

    @Override
    public void start() {
        var executors = new ExecutionStrategy[noThreads];
        for (var i = 0; i < noThreads; i++) {
            executors[i] = executorSupplier.apply(i);
            executors[i].start();
        }
        this.executors = executors;
    }

    @Override
    public void stop() {
        for (var executor : executors) {
            executor.stop();
        }
    }
}
