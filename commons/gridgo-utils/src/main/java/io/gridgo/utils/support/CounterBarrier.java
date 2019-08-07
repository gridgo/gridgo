package io.gridgo.utils.support;

import static io.gridgo.utils.ThreadUtils.busySpin;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CounterBarrier {

    public static CounterBarrier newDefault(int initValue) {
        return new CounterBarrier(initValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static CounterBarrier newDefault(int initValue, int lowerBound, int upperBound) {
        return new CounterBarrier(initValue, lowerBound, upperBound);
    }

    public static CounterBarrier newWithLowerBound(int initValue, int lowerBound) {
        return new CounterBarrier(initValue, lowerBound, Integer.MAX_VALUE);
    }

    public static CounterBarrier newWithUpperBound(int initValue, int upperBound) {
        return new CounterBarrier(initValue, Integer.MIN_VALUE, upperBound);
    }

    private final int lowerBound;
    private final int upperBound;

    private final AtomicInteger counter = new AtomicInteger(0);
    private final AtomicReference<AtomicInteger> incrementLock = new AtomicReference<>(counter);
    private final AtomicReference<AtomicInteger> decrementLock = new AtomicReference<>(counter);

    private CounterBarrier(int initValue, int lowerBound, int upperBound) {
        if (upperBound < lowerBound) {
            throw new IllegalArgumentException("upperBound cannot be less than lowerBound");
        }

        this.lowerBound = lowerBound;
        this.upperBound = upperBound;

        this.counter.set(initValue);
    }

    private void waitForUnlocked(AtomicReference<AtomicInteger> lock) {
        busySpin(() -> lock.get() == null);
    }

    public void waitForIncrementUnlocked() {
        this.waitForUnlocked(incrementLock);
    }

    public void waitForDecrementUnlocked() {
        this.waitForUnlocked(decrementLock);
    }

    public boolean lockIncrement() {
        return this.incrementLock.compareAndSet(this.counter, null);
    }

    public boolean lockDecrement() {
        return this.decrementLock.compareAndSet(this.counter, null);
    }

    public int incrementAndGet() {
        this.waitForIncrementUnlocked();
        return this.counter.accumulateAndGet(1, (currentValue, incrementBy) -> {
            int newValue = currentValue + incrementBy;
            if (newValue > upperBound) {
                return upperBound;
            }
            return newValue;
        });
    }

    public int decrementAndGet() {
        this.waitForDecrementUnlocked();
        return this.counter.accumulateAndGet(-1, (currentValue, incrementBy) -> {
            int newValue = currentValue + incrementBy;
            if (newValue < lowerBound) {
                return lowerBound;
            }
            return newValue;
        });
    }

    public int get() {
        return this.counter.get();
    }

    private final void lockAndWaitFor(int value, AtomicReference<AtomicInteger> lock) {
        if (lock.compareAndSet(this.counter, null)) {
            this.waitFor(value);
        } else {
            throw new IllegalStateException("The lock has been locked");
        }
    }

    public final void lockIncrementAndWaitFor(int value) {
        this.lockAndWaitFor(value, incrementLock);
    }

    public final void lockDecrementAndWaitFor(int value) {
        this.lockAndWaitFor(value, decrementLock);
    }

    private final void waitFor(int value) {
        busySpin(() -> counter.get() != value);
    }

    public boolean unlockIncrement() {
        return this.incrementLock.compareAndSet(null, this.counter);
    }

    public boolean unlockDecrement() {
        return this.decrementLock.compareAndSet(null, this.counter);
    }
}
