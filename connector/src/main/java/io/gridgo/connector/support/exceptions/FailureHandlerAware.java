package io.gridgo.connector.support.exceptions;

import java.util.function.Consumer;
import java.util.function.Function;

import io.gridgo.framework.support.Message;

public interface FailureHandlerAware<T> {

    public default T setFailureHandler(Consumer<Throwable> failureHandler) {
        return setFailureHandler((ex) -> {
            failureHandler.accept(ex);
            return null;
        });
    }

    public T setFailureHandler(Function<Throwable, Message> failureHandler);
}
