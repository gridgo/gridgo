package io.gridgo.utils.exception;

public class ObjectReflectiveException extends RuntimeException {

    private static final long serialVersionUID = -2569346711203369483L;

    public ObjectReflectiveException() {
        super();
    }

    public ObjectReflectiveException(String message) {
        super(message);
    }

    public ObjectReflectiveException(Throwable cause) {
        super(cause);
    }

    public ObjectReflectiveException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
