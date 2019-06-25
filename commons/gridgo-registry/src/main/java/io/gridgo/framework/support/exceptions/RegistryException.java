package io.gridgo.framework.support.exceptions;

public class RegistryException extends RuntimeException {

    private static final long serialVersionUID = -8383253748791319830L;

    public RegistryException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public RegistryException(Throwable cause) {
        super(cause);
    }
}
