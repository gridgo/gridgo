package io.gridgo.utils.exception;

public class MalformedHostAndPortException extends RuntimeException {

    private static final long serialVersionUID = 5053753177901015908L;

    public MalformedHostAndPortException(String message) {
        super(message);
    }
}
