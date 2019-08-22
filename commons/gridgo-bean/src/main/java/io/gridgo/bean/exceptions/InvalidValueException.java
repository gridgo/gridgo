package io.gridgo.bean.exceptions;

public class InvalidValueException extends RuntimeException {

    private static final long serialVersionUID = 6660727501005300081L;

    public InvalidValueException() {
        super();
    }

    public InvalidValueException(String message) {
        super(message);
    }

    public InvalidValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidValueException(Throwable cause) {
        super(cause);
    }
}
