package io.gridgo.boot.support.exceptions;

public class InitializationException extends RuntimeException {

    private static final long serialVersionUID = 185513533756870011L;

    public InitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
