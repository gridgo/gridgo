package io.gridgo.framework.execution.impl.disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.ThreadFactory;

import io.gridgo.framework.execution.impl.ExecutionContextEvent;

public class DisruptorWorkerPoolExecutionStrategy<T, H> extends AbstractDisruptorExecutionStrategy<T, H> {

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    public DisruptorWorkerPoolExecutionStrategy() {
        this(DEFAULT_BUFFER_SIZE);
    }

    public DisruptorWorkerPoolExecutionStrategy(final int bufferSize) {
        this(bufferSize, 2);
    }

    public DisruptorWorkerPoolExecutionStrategy(final int bufferSize, final int numWorkers) {
        this(bufferSize, numWorkers, new BlockingWaitStrategy());
    }

    public DisruptorWorkerPoolExecutionStrategy(final int bufferSize, final int numWorkers,
            final WaitStrategy waitStrategy) {
        this(bufferSize, numWorkers, waitStrategy, (runnable) -> {
            return new Thread(runnable);
        });
    }

    public DisruptorWorkerPoolExecutionStrategy(final int bufferSize, final int numWorkers,
            final WaitStrategy waitStrategy, final ThreadFactory threadFactory) {
        this(ProducerType.MULTI, bufferSize, numWorkers, waitStrategy, threadFactory);
    }

    @SuppressWarnings("unchecked")
    public DisruptorWorkerPoolExecutionStrategy(ProducerType type, final int bufferSize, final int numWorkers,
            final WaitStrategy waitStrategy, final ThreadFactory threadFactory) {
        this.disruptor = new Disruptor<>(ExecutionContextEvent::new, bufferSize, threadFactory, type, waitStrategy);
        WorkHandler<ExecutionContextEvent<T, H>>[] workers = new WorkHandler[numWorkers];
        for (int i = 0; i < numWorkers; i++) {
            workers[i] = event -> {
                event.getContext().execute();
            };
        }
        this.disruptor.handleEventsWithWorkerPool(workers);
    }
}
