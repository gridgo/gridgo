package io.gridgo.utils.pojo.exception;

public class PojoProxyException extends RuntimeException {

    private static final long serialVersionUID = 5537846605854706292L;

    public PojoProxyException() {
        super();
    }

    public PojoProxyException(String message) {
        super(message);
    }

    public PojoProxyException(String message, Throwable cause) {
        super(message, cause);
    }

    public PojoProxyException(Throwable cause) {
        super(cause);
    }
}
