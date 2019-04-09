package io.gridgo.connector.support.exceptions;

public class NoSubscriberException extends RuntimeException {

    private static final long serialVersionUID = 6266907384669032051L;

    public NoSubscriberException() {
        super("There is no subscribers available");
    }
}
