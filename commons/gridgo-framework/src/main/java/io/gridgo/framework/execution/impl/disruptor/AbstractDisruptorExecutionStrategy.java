package io.gridgo.framework.execution.impl.disruptor;

import com.lmax.disruptor.dsl.Disruptor;

import io.gridgo.framework.execution.ExecutionStrategy;
import io.gridgo.framework.execution.impl.ExecutionContextEvent;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.context.ExecutionContext;
import io.gridgo.framework.support.context.impl.DefaultExecutionContext;

public abstract class AbstractDisruptorExecutionStrategy<T, H> implements ExecutionStrategy {

    protected Disruptor<ExecutionContextEvent<T, H>> disruptor;

    @Override
    public void execute(Runnable runnable, Message request) {
        ExecutionContext<T, H> context = new DefaultExecutionContext<>(t -> runnable.run());
        execute(context);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void execute(ExecutionContext context) {
        this.disruptor.publishEvent((event, sequence) -> {
            event.clear();
            event.setContext(context);
        });
    }

    @Override
    public void start() {
        disruptor.start();
    }

    @Override
    public void stop() {
        disruptor.shutdown();
    }
}
