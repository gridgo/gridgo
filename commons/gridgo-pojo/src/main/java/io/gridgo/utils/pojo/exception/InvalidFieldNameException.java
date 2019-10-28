package io.gridgo.utils.pojo.exception;

public class InvalidFieldNameException extends RuntimeException {

    private static final long serialVersionUID = 1001058277288122399L;

    public InvalidFieldNameException(String message) {
        super(message);
    }

    public InvalidFieldNameException(Throwable cause) {
        super(cause);
    }

    public InvalidFieldNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFieldNameException() {
        super();
    }
}
