package io.gridgo.framework.test;

import com.lmax.disruptor.dsl.ProducerType;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import io.gridgo.framework.execution.impl.DefaultExecutionStrategy;
import io.gridgo.framework.execution.impl.ExecutorExecutionStrategy;
import io.gridgo.framework.execution.impl.disruptor.MultiProducerDisruptorExecutionStrategy;
import io.gridgo.framework.execution.impl.disruptor.SingleConsumerDisruptorExecutionStrategy;
import io.gridgo.framework.execution.impl.disruptor.SingleProducerDisruptorExecutionStrategy;

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
}
