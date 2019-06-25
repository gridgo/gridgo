package io.gridgo.framework.execution.impl;

import java.util.function.Function;
import java.util.function.Supplier;

import io.gridgo.framework.execution.ExecutionStrategy;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.context.ExecutionContext;
import lombok.NonNull;

public class HashedExecutionStrategy implements ExecutionStrategy {

    private static final Function<Message, Integer> DEFAULT_HASH_FUNCTION = Message::hashCode;

    private Supplier<ExecutionStrategy> executorSupplier;

    private Function<Message, Integer> hashFunction;

    private int noThreads;

    private ExecutionStrategy[] executors;

    public HashedExecutionStrategy(final int noThreads, Supplier<ExecutionStrategy> executorSupplier) {
        this(noThreads, executorSupplier, DEFAULT_HASH_FUNCTION);
    }

    public HashedExecutionStrategy(final int noThreads, Supplier<ExecutionStrategy> executorSupplier,
            Function<Message, Integer> hashFunction) {
        this.noThreads = noThreads;
        this.executorSupplier = executorSupplier;
        this.hashFunction = hashFunction;
    }

    @Override
    public void execute(final @NonNull Runnable runnable, Message request) {
        var hash = calculateHash(request);
        executors[hash].execute(runnable, request);
    }

    @Override
    public void execute(ExecutionContext<Message, Message> context) {
        var hash = calculateHash(context.getRequest());
        executors[hash].execute(context);
    }

    private int calculateHash(Message request) {
        if (request == null)
            return 0;
        return hashFunction.apply(request) % noThreads;
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
