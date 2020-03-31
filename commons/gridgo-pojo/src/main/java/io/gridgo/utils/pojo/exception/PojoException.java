package io.gridgo.utils.pojo.exception;

public class PojoException extends RuntimeException {

    private static final long serialVersionUID = 5537846605854706292L;

    public PojoException() {
        super();
    }

    public PojoException(String message) {
        super(message);
    }

    public PojoException(String message, Throwable cause) {
        super(message, cause);
    }

    public PojoException(Throwable cause) {
        super(cause);
    }
}
