package io.gridgo.framework.execution.impl;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import io.gridgo.framework.execution.ExecutionStrategy;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.context.ExecutionContext;

public class RoundRobinExecutionStrategy implements ExecutionStrategy {

    private final int noThreads;

    private final int mask;

    private AtomicLong counter = new AtomicLong();

    private Supplier<ExecutionStrategy> executorSupplier;

    private ExecutionStrategy[] executors;

    public RoundRobinExecutionStrategy(final int noThreads, Supplier<ExecutionStrategy> executorSupplier) {
        if (!isPowerOf2(noThreads))
            throw new IllegalArgumentException("Number of threads must be power of 2");
        this.noThreads = noThreads;
        this.mask = noThreads - 1;
        this.executorSupplier = executorSupplier;
    }

    private boolean isPowerOf2(int noThreads) {
        return (noThreads & (noThreads - 1)) == 0;
    }

    @Override
    public void execute(Runnable runnable, Message request) {
        executors[(int) Math.abs(counter.getAndIncrement() & mask)].execute(runnable, request);
    }

    @Override
    public void execute(ExecutionContext<Message, Message> context) {
        executors[(int) Math.abs(counter.getAndIncrement() & mask)].execute(context);
    }

    @Override
    public void start() {
        var executors = new ExecutionStrategy[noThreads];
        for (var i = 0; i < noThreads; i++) {
            executors[i] = executorSupplier.get();
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
