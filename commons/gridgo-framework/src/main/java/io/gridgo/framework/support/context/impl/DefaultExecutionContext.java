package io.gridgo.framework.support.context.impl;

import java.util.function.Consumer;

import org.joo.promise4j.Promise;

import io.gridgo.framework.support.context.ExecutionContext;

public class DefaultExecutionContext<T, H> implements ExecutionContext<T, H> {

    private T request;

    private Consumer<T> handler;

    private Promise<H, Exception> promise;

    public DefaultExecutionContext(Consumer<T> handler) {
        this.handler = handler;
    }

    public DefaultExecutionContext(T request, Consumer<T> handler, Promise<H, Exception> promise) {
        this.request = request;
        this.handler = handler;
        this.promise = promise;
    }

    @Override
    public void execute() {
        handler.accept(request);
    }

    @Override
    public Promise<H, Exception> promise() {
        return promise;
    }

    @Override
    public T getRequest() {
        return request;
    }
}
