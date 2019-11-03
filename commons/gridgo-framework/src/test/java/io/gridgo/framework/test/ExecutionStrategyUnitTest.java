package io.gridgo.framework.test;

import com.lmax.disruptor.dsl.ProducerType;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import io.gridgo.framework.execution.impl.DefaultExecutionStrategy;
import io.gridgo.framework.execution.impl.ExecutorExecutionStrategy;
import io.gridgo.framework.execution.impl.HashedExecutionStrategy;
import io.gridgo.framework.execution.impl.RoundRobinExecutionStrategy;
import io.gridgo.framework.execution.impl.disruptor.MultiProducerDisruptorExecutionStrategy;
import io.gridgo.framework.execution.impl.disruptor.SingleConsumerDisruptorExecutionStrategy;
import io.gridgo.framework.execution.impl.disruptor.SingleProducerDisruptorExecutionStrategy;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.context.ExecutionContext;
import io.gridgo.framework.support.context.impl.DefaultExecutionContext;
import lombok.NonNull;

public class ExecutionStrategyUnitTest {

    @Test
    public void testStrategy() throws InterruptedException {
        var atomic = new AtomicInteger(0);
        var s1 = new DefaultExecutionStrategy();
        s1.execute(atomic::incrementAndGet);
        Assert.assertEquals(1, atomic.get());

        var latch = new CountDownLatch(10);
        var s2 = new ExecutorExecutionStrategy(5);
        s2.start();
        for (int i = 0; i < 10; i++) {
            s2.execute(() -> {
                latch.countDown();
            });
        }
        latch.await();
        s2.stop();
    }

    @Test
    public void testDisruptorStrategy() throws InterruptedException {
        var latch2 = new CountDownLatch(10);
        var s3 = new MultiProducerDisruptorExecutionStrategy<>();
        s3.start();
        for (int i = 0; i < 10; i++) {
            s3.execute(() -> {
                latch2.countDown();
            });
        }
        latch2.await();
        s3.stop();

        var latch3 = new CountDownLatch(10);
        var s4 = new SingleProducerDisruptorExecutionStrategy<>();
        s4.start();
        for (int i = 0; i < 10; i++) {
            s4.execute(() -> {
                latch3.countDown();
            });
        }
        latch3.await();
        s4.stop();

        var latch4 = new CountDownLatch(10);
        var s5 = new SingleConsumerDisruptorExecutionStrategy<>(ProducerType.SINGLE);
        s5.start();
        for (int i = 0; i < 10; i++) {
            s5.execute(() -> {
                latch4.countDown();
            });
        }
        latch4.await();
        s5.stop();
    }

    @Test
    public void testHashedStrategy() throws InterruptedException {
        var eses = Arrays.asList( //
                new CustomExecutionStrategy(1), //
                new CustomExecutionStrategy(2) //
        );
        var counter = new AtomicInteger();
        var es = new HashedExecutionStrategy(2, eses::get, msg -> msg.body().asValue().getInteger());
        es.start();
        es.execute(new DefaultExecutionContext<>(Message.ofAny(1), msg -> {
            counter.incrementAndGet();
        }, null));
        es.execute(new DefaultExecutionContext<>(Message.ofAny(2), msg -> {
            counter.incrementAndGet();
        }, null));
        es.execute(() -> {
            counter.incrementAndGet();
        }, Message.ofAny(3));
        es.execute(() -> {
            counter.incrementAndGet();
        }, Message.ofAny(4));

        for (var ces : eses) {
            Assert.assertEquals(2, ces.counter);
        }
        Assert.assertEquals(4, counter.get());

        es.execute(() -> {});
        Assert.assertEquals(3, eses.get(0).counter);

        es.execute(() -> {});
        Assert.assertEquals(4, eses.get(0).counter);

        es.stop();
    }

    @Test
    public void testRRStrategy() throws InterruptedException {
        var eses = Arrays.asList( //
                new CustomExecutionStrategy(1), //
                new CustomExecutionStrategy(2) //
        );
        var counter = new AtomicInteger();
        var es = new RoundRobinExecutionStrategy(2, eses::get);
        es.start();
        es.execute(new DefaultExecutionContext<>(Message.ofAny(1), msg -> {
            counter.incrementAndGet();
        }, null));
        es.execute(new DefaultExecutionContext<>(Message.ofAny(2), msg -> {
            counter.incrementAndGet();
        }, null));
        es.execute(() -> {
            counter.incrementAndGet();
        }, Message.ofAny(3));
        es.execute(() -> {
            counter.incrementAndGet();
        }, Message.ofAny(4));

        for (var ces : eses) {
            Assert.assertEquals(2, ces.counter);
        }
        Assert.assertEquals(4, counter.get());

        es.execute(() -> {});
        Assert.assertEquals(3, eses.get(0).counter);

        es.execute(() -> {});
        Assert.assertEquals(3, eses.get(1).counter);

        es.stop();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRRInvalidThreads() {
        new RoundRobinExecutionStrategy(3, CustomExecutionStrategy::new);
    }

    class CustomExecutionStrategy extends DefaultExecutionStrategy {

        private int counter;

        public CustomExecutionStrategy(int threadNumber) {
            // Nothing to do
        }

        @Override
        public void execute(final @NonNull Runnable runnable, Message request) {
            super.execute(runnable, request);
            this.counter++;
        }

        @Override
        public void execute(ExecutionContext<Message, Message> context) {
            super.execute(context);
            this.counter++;
        }
    }
}
