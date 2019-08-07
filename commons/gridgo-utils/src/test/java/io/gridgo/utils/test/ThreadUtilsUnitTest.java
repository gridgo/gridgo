package io.gridgo.utils.test;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.utils.ThreadUtils;

public class ThreadUtilsUnitTest {

    @Test
    public void testSimple() {
        var disposer = ThreadUtils.registerShutdownTask(() -> System.out.println("Shutting down..."));
        Assert.assertNotEquals(-1, disposer);
        var bool = disposer.dispose();
        Assert.assertTrue(bool);

        ThreadUtils.sleep(0);
        ThreadUtils.sleepSilence(0);

        var atomic = new AtomicInteger(0);
        ThreadUtils.busySpin(() -> atomic.getAndDecrement() == 0);

        new TestThreadUtils().testShutdown();
    }

    class TestThreadUtils extends ThreadUtils {
        public void testShutdown() {
            doShutdown();
        }
    }
}
