package io.gridgo.utils.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static io.gridgo.utils.ThreadUtils.busySpinUntilFalse;
import static io.gridgo.utils.ThreadUtils.busySpinUntilTrue;
import static io.gridgo.utils.ThreadUtils.registerShutdownTask;

import io.gridgo.utils.ThreadUtils;
import io.gridgo.utils.exception.ThreadingException;

public class ThreadUtilsUnitTest {

    class TestThreadUtils extends ThreadUtils {
        public void testShutdown() {
            doShutdown();
        }
    }

    @Test(expected = ThreadingException.class)
    public void testInterruptedSleep() {
        Thread.currentThread().interrupt();
        ThreadUtils.sleep(500);
        Assert.assertTrue(Thread.currentThread().isInterrupted());
    }

    @Test
    public void testSimple() {
        // clear the interrupt flag
        Thread.interrupted();

        var disposer = registerShutdownTask(() -> System.out.println("Shutting down..."));
        Assert.assertNotNull(disposer);
        Assert.assertTrue(disposer.dispose());

        ThreadUtils.sleep(0);
        ThreadUtils.sleepSilence(0);

        busySpinUntilTrue(new AtomicBoolean(true));
        busySpinUntilFalse(new AtomicBoolean(false));

        new TestThreadUtils().testShutdown();
    }
}
