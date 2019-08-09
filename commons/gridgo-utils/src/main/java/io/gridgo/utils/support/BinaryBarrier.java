package io.gridgo.utils.support;

import static io.gridgo.utils.ThreadUtils.busySpin;
import static io.gridgo.utils.support.BinaryBarrier.BinaryBarrierState.DONE;
import static io.gridgo.utils.support.BinaryBarrier.BinaryBarrierState.NONE;
import static io.gridgo.utils.support.BinaryBarrier.BinaryBarrierState.PROCESSING;

import java.util.concurrent.atomic.AtomicReference;

import lombok.AllArgsConstructor;

/**
 * Triple states non-blocking and lock-free barrier for anti-race-condition
 * issue
 * 
 * There're 3 states:
 * <ol>
 * <li><b>NONE</b> mean this barrier is not started, can only change to
 * <b>PROCESSING</b> state by invoking <b>{@link #start()}</b></li>
 * <li><b>PROCESSING</b> mean this barrier is marked as in processing state, can
 * only change to <b>DONE</b> state by invoking <b>{@link #done()}</b></li>
 * <li><b>DONE</b> mean this barrier was done its process and didn't got reset,
 * can only change to <b>NONE</b> state by invoking
 * <b>{@link #doneAndReset()}</b></li>
 * </ol>
 * 
 * @author bachden
 *
 */
@AllArgsConstructor
public final class BinaryBarrier {

    public static enum BinaryBarrierState {
        NONE, PROCESSING, DONE;
    }

    private final AtomicReference<BinaryBarrierState> state = new AtomicReference<>(NONE);

    public BinaryBarrierState getState() {
        return this.state.get();
    }

    /**
     * change state from NONE to PROCESSING
     * 
     * @return true if success, false otherwise
     */
    public boolean start() {
        return this.state.compareAndSet(NONE, PROCESSING);
    }

    /**
     * reset this barrier's state from DONE to NONE
     * 
     * @return true if success, false otherwise
     */
    public boolean reset() {
        return this.state.compareAndSet(DONE, NONE);
    }

    /**
     * change state from PROCESSING to DONE
     * 
     * @return true if success, false otherwise
     */
    public boolean done() {
        return this.state.compareAndSet(PROCESSING, DONE);
    }

    /**
     * change state from PROCESSING to NONE
     * 
     * @return true if success, false otherwise
     */
    public boolean doneAndReset() {
        return this.state.compareAndSet(PROCESSING, NONE);
    }

    /**
     * if this barrier is in PROCESSING, invoking thread will be locked by a busy
     * spin to wait until DONE if this barrier was DONE, return true immediately if
     * this barrier is NONE, return false
     * 
     * @return true if processing is done, false otherwise (barrier is not started)
     */
    public boolean waitForProcessingToDone() {
        waitForProcessing();
        return this.isDone();
    }

    /**
     * if this barrier is in PROCESSING, invoking thread will be locked by a busy
     * spin to wait until DONE if this barrier is DONE, return false immediately if
     * this barrier is NONE, return true
     * 
     * @return true if processing is done, false otherwise (barrier is not started)
     */
    public boolean waitForProcessingToReset() {
        this.waitForProcessing();
        return this.isNone();
    }

    /**
     * busy spin using Thread.onSpinWait()
     */
    private void waitForProcessing() {
        busySpin(this::isProcessing);
    }

    /**
     * check if barrier is in NONE state
     */
    public boolean isNone() {
        return this.getState() == NONE;
    }

    /**
     * check if barrier is in PROCESSING state
     */
    public boolean isProcessing() {
        return this.getState() == PROCESSING;
    }

    /**
     * check if barrier is in DONE state
     */
    public boolean isDone() {
        return this.getState() == DONE;
    }

    /**
     * check (no waiting) if this barrier's status is NONE, apply input runnable
     * 
     * @param runnable to be executed
     * @return true if runnable got executed, false otherwise
     */
    public boolean tryApply(Runnable runnable) {
        // this barrier's state should be NONE
        if (this.start()) {
            runnable.run();
            return true;
        }
        return false;
    }

    /**
     * check (no waiting) if state is NONE, apply input runnable then reset to NONE
     * 
     * @param runnable to be executed
     * @return true if runnable got executed, false otherwise
     */
    public boolean tryApplyThenReset(Runnable runnable) {
        if (this.tryApply(runnable)) {
            this.doneAndReset();
            return true;
        }
        return false;
    }

    /**
     * try to apply input runnable then set to DONE
     * 
     * @param runnable to be executed
     * @return true if runnable got executed, false otherwise
     */
    public boolean tryApplyThenDone(Runnable runnable) {
        if (this.tryApply(runnable)) {
            this.done();
            return true;
        }
        return false;
    }

    /**
     * Wait for processing to be reset to NONE then try to execute input runnable,
     * reset after all
     * 
     * @param runnable to be executed
     * 
     * @return true if runnable got executed, false otherwise
     */
    public boolean waitAndTryApply(Runnable runnable) {
        while (true) {
            if (this.waitForProcessingToReset()) {
                if (this.tryApply(runnable)) {
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    /**
     * Wait for processing to be reset to NONE then try to execute input runnable,
     * reset after all
     * 
     * @param runnable work to be executed
     * 
     * @return true if runnable got executed, false otherwise
     */
    public boolean waitAndTryApplyThenReset(Runnable runnable) {
        while (true) {
            if (this.waitForProcessingToReset()) {
                if (this.tryApplyThenReset(runnable)) {
                    // if applied, return true
                    return true;
                }
                // else, continue waiting
            } else {
                return false;
            }
        }
    }
}
