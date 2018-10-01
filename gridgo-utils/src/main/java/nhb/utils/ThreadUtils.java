package nhb.utils;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Supplier;

import nhb.utils.exception.ThreadingException;

public class ThreadUtils {

	private final static AtomicBoolean SHUTTING_DOWN_SIGNAL = new AtomicBoolean(false);
	static {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			SHUTTING_DOWN_SIGNAL.set(true);
		}));
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
			throw new ThreadingException("Interupted while sleeping", e);
		}
	}

	/**
	 * Stop current thread using LockSupport.parkNanos(nanoSegment) calling inside a
	 * while loop <br/>
	 * Break if process is shutdown or breakSignal return true
	 * 
	 * @param nanoSegment parking time in nano seconds
	 * @param breakSignal continue when this supplier return false, break loop and
	 *                    return on true
	 */
	public static final void busySpin(long nanoSegment, Supplier<Boolean> breakSignal) {
		while (!isShuttingDown() && !breakSignal.get()) {
			LockSupport.parkNanos(nanoSegment);
		}
	}
}
