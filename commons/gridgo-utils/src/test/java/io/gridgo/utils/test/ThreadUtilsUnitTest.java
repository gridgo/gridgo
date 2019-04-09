package io.gridgo.utils.test;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.utils.ThreadUtils;

public class ThreadUtilsUnitTest {

    @Test
    public void testSimple() {
        int id = ThreadUtils.registerShutdownTask(() -> System.out.println("Shutting down..."));
        Assert.assertNotEquals(-1, id);
        boolean bool = ThreadUtils.deregisterShutdownTask(id);
        Assert.assertTrue(bool);
        ThreadUtils.registerShutdownTask(() -> System.out.println("Shutting down..."));
        ThreadUtils.sleep(0);
        var atomic = new AtomicInteger(0);
        ThreadUtils.busySpin(0, () -> atomic.getAndDecrement() == 0);
        new TestThreadUtils().testShutdown();
    }

    class TestThreadUtils extends ThreadUtils {
        public void testShutdown() {
            doShutdown();
        }
    }
}
