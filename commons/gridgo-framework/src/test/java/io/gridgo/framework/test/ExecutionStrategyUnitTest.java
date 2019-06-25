package io.gridgo.framework.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.framework.execution.impl.DefaultExecutionStrategy;
import io.gridgo.framework.execution.impl.ExecutorExecutionStrategy;
import io.gridgo.framework.execution.impl.disruptor.MultiProducerDisruptorExecutionStrategy;

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
    }
}
