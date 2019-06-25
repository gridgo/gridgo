package io.gridgo.framework.execution.impl.disruptor;

import java.util.concurrent.ThreadFactory;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

public class MultiProducerDisruptorExecutionStrategy<T, H> extends SingleConsumerDisruptorExecutionStrategy<T, H> {

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    public MultiProducerDisruptorExecutionStrategy() {
        this(DEFAULT_BUFFER_SIZE);
    }

    public MultiProducerDisruptorExecutionStrategy(final int bufferSize) {
        this(bufferSize, new BlockingWaitStrategy());
    }

    public MultiProducerDisruptorExecutionStrategy(final int bufferSize, final WaitStrategy waitStrategy) {
        this(bufferSize, waitStrategy, (runnable) -> {
            return new Thread(runnable);
        });
    }

    public MultiProducerDisruptorExecutionStrategy(final int bufferSize, final WaitStrategy waitStrategy,
            final ThreadFactory threadFactory) {
        super(ProducerType.MULTI, bufferSize, waitStrategy, threadFactory);
    }
}
