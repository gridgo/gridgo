package io.gridgo.utils.exception;

public class AssertionException extends RuntimeException {

    private static final long serialVersionUID = 2187325525005917602L;

    public AssertionException() {
        super();
    }

    public AssertionException(String message) {
        super(message);
    }

    public AssertionException(String message, Throwable cause) {
        super(message, cause);
    }

    public AssertionException(Throwable cause) {
        super(cause);
    }
}
