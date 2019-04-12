package io.gridgo.utils.exception;

public class MalformedHostAndPortException extends RuntimeException {

    private static final long serialVersionUID = 5053753177901015908L;

    public MalformedHostAndPortException(String message) {
        super(message);
    }

    public MalformedHostAndPortException(Throwable cause) {
        super(cause);
    }

    public MalformedHostAndPortException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformedHostAndPortException() {
        super();
    }
}
