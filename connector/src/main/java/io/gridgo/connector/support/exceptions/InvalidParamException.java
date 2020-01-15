package io.gridgo.connector.support.exceptions;

public class InvalidParamException extends RuntimeException {

    private static final long serialVersionUID = 1882627679265529060L;

    public InvalidParamException(String message) {
        super(message);
    }
}
