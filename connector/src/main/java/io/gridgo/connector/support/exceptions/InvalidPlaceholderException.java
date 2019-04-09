package io.gridgo.connector.support.exceptions;

public class InvalidPlaceholderException extends RuntimeException {

    private static final long serialVersionUID = 1882627679265529060L;

    public InvalidPlaceholderException() {
        super();
    }

    public InvalidPlaceholderException(String message) {
        super(message);
    }

    public InvalidPlaceholderException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPlaceholderException(Throwable cause) {
        super(cause);
    }
}
