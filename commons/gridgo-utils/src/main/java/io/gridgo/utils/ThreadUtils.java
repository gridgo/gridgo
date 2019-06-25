package io.gridgo.utils;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Supplier;

import org.cliffc.high_scale_lib.NonBlockingHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.gridgo.utils.exception.ThreadingException;
import io.gridgo.utils.helper.Assert;

public class ThreadUtils {

    private final static AtomicBoolean SHUTTING_DOWN_SIGNAL = new AtomicBoolean(false);

    private final static AtomicInteger shutdownTaskIdSeed = new AtomicInteger(0);
    private final static Map<Integer, Runnable> shutdownTasks = new NonBlockingHashMap<>();
    private final static Logger logger = LoggerFactory.getLogger(ThreadUtils.class);

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(ThreadUtils::doShutdown));
    }

    protected static void doShutdown() {
        SHUTTING_DOWN_SIGNAL.set(true);
        // process shutdown tasks...
        int maxId = Integer.MIN_VALUE;
        for (Integer key : shutdownTasks.keySet()) {
            if (key > maxId) {
                maxId = key;
            }
        }

        for (int i = 0; i <= maxId; i++) {
            Runnable task = shutdownTasks.get(i);
            if (task != null) {
                try {
                    task.run();
                } catch (Exception e) {
                    logger.warn("Exception caught while shutting down", e);
                }
            }
        }
    }

    /**
     * Return current process's state
     * 
     * @return true if process is shutting down, false otherwise
     */
    public static boolean isShuttingDown() {
        return SHUTTING_DOWN_SIGNAL.get();
    }

    /**
     * Sleep current thread inside try, catch block
     * 
     * @param millis time to sleep
     * @throws ThreadingException on interrupted
     */
    public static final void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ThreadingException("Interupted while sleeping", e);
        }
    }

    /**
     * Stop current thread using LockSupport.parkNanos(nanoSegment) calling inside a
     * while loop <br>
     * Break if process is shutdown or breakSignal return true
     * 
     * @param nanoSegment        parking time in nano seconds
     * @param continueUntilFalse continue spin when this supplier return true, break
     *                           loop and return when false
     */
    public static final void busySpin(long nanoSegment, Supplier<Boolean> continueUntilFalse) {
        while (!isShuttingDown() && continueUntilFalse.get()) {
            LockSupport.parkNanos(nanoSegment);
        }
    }

    /**
     * register a task which can be processed when process shutdown
     * 
     * @param task
     * @return task id use to remove the registered task, -1 if false to register
     */
    public static int registerShutdownTask(Runnable task) {
        if (!SHUTTING_DOWN_SIGNAL.get()) {
            Assert.notNull(task, "Shutdown task");
            int id = shutdownTaskIdSeed.getAndIncrement();
            shutdownTasks.put(id, task);
            return id;
        }
        return -1;
    }

    /**
     * deregister a registered shutdown task
     * 
     * @param id
     * @return true if successful
     */
    public static boolean deregisterShutdownTask(int id) {
        if (!SHUTTING_DOWN_SIGNAL.get() && shutdownTasks.containsKey(id)) {
            shutdownTasks.remove(id);
            return true;
        }
        return false;
    }
}
