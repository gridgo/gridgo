package io.gridgo.connector.test;

import org.joo.promise4j.Promise;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import io.gridgo.connector.impl.SingleThreadSendingProducer;
import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.connector.support.config.impl.DefaultConnectorContext;
import io.gridgo.framework.support.Message;

public class SingleThreadSendingProducerUnitTest {

    @Test
    public void testSendByCountDown() throws InterruptedException {
        int count = 10;
        var latch = new CountDownLatch(count);
        var producer = new AbstractSingleThreadSendingProducer(new DefaultConnectorContext(), 1024, false, 0) {

            @Override
            protected void executeSendOnSingleThread(Message message) throws Exception {
                latch.countDown();
            }
        };
        producer.start();
        var executor = Executors.newFixedThreadPool(8);
        for (int i = 0; i < count; i++) {
            int cur = i;
            executor.submit(() -> {
                producer.send(Message.ofAny(cur));
            });
        }
        latch.await();
        producer.stop();
        executor.shutdownNow();
    }

    @Test
    public void testSendByCounter() throws InterruptedException {
        int count = 10;
        var producer = new CounterProducer(new DefaultConnectorContext(), 1024, false, 0);
        producer.start();
        var latch = new CountDownLatch(count);
        var executor = Executors.newFixedThreadPool(8);
        for (int i = 0; i < count; i++) {
            int cur = i;
            executor.submit(() -> {
                producer.sendWithAck(Message.ofAny(cur)) //
                        .always((s, r, e) -> latch.countDown());
            });
        }
        latch.await();
        producer.stop();
        executor.shutdownNow();
        Assert.assertEquals(count * (count - 1) / 2, producer.counter);
    }

    @Test
    public void testSendByCounterWithBatch() throws InterruptedException {
        int count = 100;
        var producer = new CounterProducer(new DefaultConnectorContext(), 1024, true, 10);
        producer.start();
        var latch = new CountDownLatch(count);
        var executor = Executors.newFixedThreadPool(8);
        for (int i = 0; i < count; i++) {
            int cur = i;
            executor.submit(() -> {
                producer.sendWithAck(Message.ofAny(cur)) //
                        .always((s, r, e) -> latch.countDown());
            });
        }
        latch.await();
        producer.stop();
        executor.shutdownNow();
        Assert.assertEquals(count * (count - 1) / 2, producer.counter);
    }

    abstract class AbstractSingleThreadSendingProducer extends SingleThreadSendingProducer {

        protected AbstractSingleThreadSendingProducer(ConnectorContext context, int ringBufferSize,
                boolean batchingEnabled, int maxBatchSize) {
            super(context, ringBufferSize, batchingEnabled, maxBatchSize);
        }

        @Override
        public Promise<Message, Exception> call(Message message) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isCallSupported() {
            return false;
        }

        @Override
        protected String generateName() {
            return null;
        }
    }

    class CounterProducer extends AbstractSingleThreadSendingProducer {

        private int counter = 0;

        protected CounterProducer(ConnectorContext context, int ringBufferSize, boolean batchingEnabled,
                int maxBatchSize) {
            super(context, ringBufferSize, batchingEnabled, maxBatchSize);
        }

        @Override
        protected void executeSendOnSingleThread(Message message) throws Exception {
            counter += message.body().asValue().getInteger();
        }

        @Override
        protected Message accumulateBatch(Collection<Message> messages) {
            var count = 0;
            for (var msg : messages) {
                count += msg.body().asValue().getInteger();
            }
            return Message.ofAny(count);
        }
    };
}
