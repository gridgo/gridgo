package io.gridgo.framework.execution.impl;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import io.gridgo.framework.execution.ExecutionStrategy;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.context.ExecutionContext;

public class RoundRobinExecutionStrategy extends AbstractMultiExecutionStrategy {

    private final int mask;

    private AtomicLong counter = new AtomicLong();

    public RoundRobinExecutionStrategy(final int noThreads, Function<Integer, ExecutionStrategy> executorSupplier) {
        super(noThreads, executorSupplier);
        if (!isPowerOf2(noThreads))
            throw new IllegalArgumentException("Number of threads must be power of 2");
        this.mask = noThreads - 1;
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
}
