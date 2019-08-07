package io.gridgo.utils;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.onSpinWait;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import io.gridgo.utils.exception.ThreadingException;
import lombok.NonNull;

public class ThreadUtils {

    @FunctionalInterface
    public static interface ShutdownTaskDisposable {
        boolean dispose();
    }

    private final static AtomicBoolean SHUTTING_DOWN_SIGNAL = new AtomicBoolean(false);

    private final static AtomicInteger shutdownTaskIdSeed = new AtomicInteger(0);
    private final static Map<Integer, List<Runnable>> shutdownTasks = new NonBlockingHashMap<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(ThreadUtils::doShutdown, "SHUTDOWN HOOK"));
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
            List<Runnable> tasks = shutdownTasks.get(i);
            if (tasks != null) {
                for (var task : tasks) {
                    try {
                        task.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
            currentThread().interrupt();
            throw new ThreadingException("Interupted while sleeping", e);
        }
    }

    /**
     * Sleep current thread without exception throwing
     * 
     * @param millis time to sleep
     * 
     * @return false if current thread got interrupted, true otherwise
     */
    public static final boolean sleepSilence(long millis) {
        try {
            Thread.sleep(millis);
            return true;
        } catch (InterruptedException e) {
            // ignore
        }
        return false;
    }

    /**
     * Stop current thread using LockSupport.parkNanos(nanoSegment) calling inside a
     * while loop <br>
     * Break if process/currentThread shutdown or breakSignal return true
     * 
     * @param continueUntilFalse continue spin when this supplier return true, break
     *                           loop and return when false
     */
    public static final void busySpin(Supplier<Boolean> continueUntilFalse) {
        while (continueUntilFalse.get()) {
            if (isShuttingDown() || currentThread().isInterrupted())
                break;
            onSpinWait();
        }
    }

    /**
     * register a task which can be processed when process shutdown
     * 
     * @param task the task to be registered
     * @return disposable object
     */
    public static ShutdownTaskDisposable registerShutdownTask(@NonNull Runnable task) {
        return registerShutdownTask(task, shutdownTaskIdSeed.incrementAndGet());
    }

    public static ShutdownTaskDisposable registerShutdownTask(@NonNull Runnable task, int order) {
        if (isShuttingDown()) {
            return null;
        }

        shutdownTasks.computeIfAbsent(order, key -> new CopyOnWriteArrayList<Runnable>()).add(task);
        return () -> {
            if (isShuttingDown()) {
                return false;
            }

            var tasks = shutdownTasks.get(order);
            if (tasks == null) {
                return false;
            }

            return tasks.remove(task);
        };
    }
}
