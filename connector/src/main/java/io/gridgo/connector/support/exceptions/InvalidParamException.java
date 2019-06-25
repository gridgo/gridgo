package io.gridgo.connector.support.exceptions;

public class InvalidParamException extends RuntimeException {

    private static final long serialVersionUID = 1882627679265529060L;

    public InvalidParamException() {
        super();
    }

    public InvalidParamException(String message) {
        super(message);
    }

    public InvalidParamException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidParamException(Throwable cause) {
        super(cause);
    }
}
