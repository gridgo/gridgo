package io.gridgo.connector.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadFactory;

import org.joo.promise4j.Deferred;
import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.AsyncDeferredObject;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;

import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.framework.support.Message;
import lombok.Getter;

public abstract class SingleThreadSendingProducer extends AbstractProducer {

    private final Disruptor<ProducerEvent> sendWorker;

    @Getter
    private final boolean batchingEnabled;

    private final int maxBatchSize;

    private final EventHandler<ProducerEvent> sender = new EventHandler<ProducerEvent>() {

        private final List<Message> batch = new LinkedList<>();

        private final List<Deferred<Message, Exception>> deferreds = new LinkedList<>();

        @Override
        public void onEvent(ProducerEvent event, long sequence, boolean endOfBatch) throws Exception {
            Message message = null;

            if (isBatchingEnabled()) {
                batch.add(event.getMessage());
                deferreds.add(event.getDeferred());
                if (endOfBatch || (batch.size() >= maxBatchSize)) {
                    message = accumulateBatch(batch);
                    batch.clear();
                }
            } else {
                message = event.getMessage();
            }

            if (message != null) {
                Exception exception = null;
                try {
                    executeSendOnSingleThread(message);
                } catch (Exception e) {
                    exception = e;
                } finally {
                    if (!isBatchingEnabled()) {
                        ack(event.getDeferred(), exception);
                    } else {
                        for (var deferred : deferreds) {
                            ack(deferred);
                        }
                        deferreds.clear();
                    }
                }
            }
        }
    };

    protected SingleThreadSendingProducer(ConnectorContext context, int ringBufferSize, boolean batchingEnabled, int maxBatchSize) {
        this(context, ringBufferSize, Thread::new, batchingEnabled, maxBatchSize);
    }

    protected SingleThreadSendingProducer(ConnectorContext context, int ringBufferSize, ThreadFactory threadFactory, boolean batchingEnabled,
            int maxBatchSize) {
        super(context);
        this.sendWorker = new Disruptor<>(ProducerEvent::new, ringBufferSize, threadFactory);
        this.sendWorker.handleEventsWith(sender);
        this.batchingEnabled = batchingEnabled;
        this.maxBatchSize = maxBatchSize;
    }

    /**
     * called when batchingEnabled
     * 
     * @param messages list of sub-messages
     * @return batched message
     */
    protected Message accumulateBatch(Collection<Message> messages) {
        throw new UnsupportedOperationException("Method must be overrided by sub class");
    }

    protected Deferred<Message, Exception> createDeferred() {
        return new AsyncDeferredObject<>();
    }

    protected abstract void executeSendOnSingleThread(Message message) throws Exception;

    @Override
    protected void onStart() {
        this.sendWorker.start();
    }

    @Override
    protected void onStop() {
        this.sendWorker.shutdown();

    }

    private void produceEvent(Message message, Deferred<Message, Exception> deferred) {
        if (!this.isStarted()) {
            return;
        }
        this.sendWorker.publishEvent((ProducerEvent event, long sequence) -> {
            event.clear();
            event.setDeferred(deferred);
            event.setMessage(message);
        });
    }

    @Override
    public final void send(Message message) {
        this.produceEvent(message, null);
    }

    @Override
    public final Promise<Message, Exception> sendWithAck(Message message) {
        var deferred = createDeferred();
        this.produceEvent(message, deferred);
        return deferred.promise();
    }
}
