package io.gridgo.utils.pojo.exception;

public class RuntimeReflectiveOperationException extends RuntimeException {

    private static final long serialVersionUID = -4630793938933767661L;

    public RuntimeReflectiveOperationException() {
        super();
    }

    public RuntimeReflectiveOperationException(Throwable e) {
        super(e);
    }

    public RuntimeReflectiveOperationException(String message) {
        super(message);
    }

    public RuntimeReflectiveOperationException(String message, Throwable e) {
        super(message, e);
    }

}
