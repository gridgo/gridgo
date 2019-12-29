package io.gridgo.bean.exceptions;

public class InvalidTypeException extends RuntimeException {

    private static final long serialVersionUID = 2985464912066462611L;

    public InvalidTypeException(String message) {
        super(message);
    }
}
