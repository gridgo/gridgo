package io.gridgo.utils.exception;

public class TypeMismatchException extends RuntimeException {

    private static final long serialVersionUID = 820000703246160736L;

    public TypeMismatchException() {
        super();
    }

    public TypeMismatchException(String message) {
        super(message);
    }

    public TypeMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeMismatchException(Throwable cause) {
        super(cause);
    }
}
