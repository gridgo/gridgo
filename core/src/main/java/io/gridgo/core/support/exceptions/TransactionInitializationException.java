package io.gridgo.core.support.exceptions;

public class TransactionInitializationException extends RuntimeException {

    private static final long serialVersionUID = 8210180685241079884L;

    public TransactionInitializationException(String msg) {
        super(msg);
    }

    public TransactionInitializationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
