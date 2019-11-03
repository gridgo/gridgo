package io.gridgo.framework.execution.impl.disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.ThreadFactory;

import io.gridgo.framework.execution.impl.ExecutionContextEvent;

public class SingleConsumerDisruptorExecutionStrategy<T, H> extends AbstractDisruptorExecutionStrategy<T, H> {

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    public SingleConsumerDisruptorExecutionStrategy(ProducerType producerType) {
        this(producerType, DEFAULT_BUFFER_SIZE);
    }

    public SingleConsumerDisruptorExecutionStrategy(ProducerType producerType, final int bufferSize) {
        this(producerType, bufferSize, new BlockingWaitStrategy());
    }

    public SingleConsumerDisruptorExecutionStrategy(ProducerType producerType, final int bufferSize,
            final WaitStrategy waitStrategy) {
        this(producerType, bufferSize, waitStrategy, (runnable) -> {
            return new Thread(runnable);
        });
    }

    public SingleConsumerDisruptorExecutionStrategy(ProducerType producerType, final int bufferSize,
            final WaitStrategy waitStrategy, final ThreadFactory threadFactory) {
        this.disruptor = new Disruptor<>(ExecutionContextEvent::new, bufferSize, threadFactory, producerType,
                waitStrategy);
        this.disruptor.handleEventsWith(this::onEvent);
    }

    private void onEvent(ExecutionContextEvent<?, ?> event, long sequence, boolean endOfBatch) {
        event.getContext().execute();
    }
}