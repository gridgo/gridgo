package io.gridgo.connector.support;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;

@Getter
public class ConnectionRef<T> {

    private T connection;

    private AtomicInteger refCount = new AtomicInteger(0);

    public ConnectionRef(T connection) {
        this.connection = connection;
    }

    public int deref() {
        int value = -1;
        int ref = 0;
        do {
            value = refCount.get();
            ref = value > 0 ? value - 1 : 0;
        } while (!refCount.compareAndSet(value, ref));
        return ref;
    }

    public void dispose() {
        this.connection = null;
    }

    public int getRefCount() {
        return refCount.get();
    }

    public int ref() {
        return refCount.incrementAndGet();
    }
}
