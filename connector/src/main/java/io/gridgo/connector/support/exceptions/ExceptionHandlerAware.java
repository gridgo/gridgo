package io.gridgo.connector.support.exceptions;

import java.util.function.Consumer;

public interface ExceptionHandlerAware<T> {

    public T setExceptionHandler(Consumer<Throwable> exceptionHandler);
}
