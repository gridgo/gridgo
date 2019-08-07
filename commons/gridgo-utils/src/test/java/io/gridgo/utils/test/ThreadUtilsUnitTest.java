package io.gridgo.utils.test;

import static io.gridgo.utils.ThreadUtils.busySpinUntilFalse;
import static io.gridgo.utils.ThreadUtils.busySpinUntilTrue;
import static io.gridgo.utils.ThreadUtils.registerShutdownTask;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.utils.ThreadUtils;

public class ThreadUtilsUnitTest {

    class TestThreadUtils extends ThreadUtils {
        public void testShutdown() {
            doShutdown();
        }
    }

    @Test
    public void testSimple() {
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
