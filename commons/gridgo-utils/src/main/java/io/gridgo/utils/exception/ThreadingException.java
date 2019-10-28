package io.gridgo.utils.exception;

public class ThreadingException extends RuntimeException {

    private static final long serialVersionUID = 2985464912066462611L;

    public ThreadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
