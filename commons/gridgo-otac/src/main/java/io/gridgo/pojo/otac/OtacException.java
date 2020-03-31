package io.gridgo.pojo.otac;

public class OtacException extends RuntimeException {

    private static final long serialVersionUID = -7263182605019223580L;

    public OtacException() {
        super();
    }

    public OtacException(String message) {
        super(message);
    }

    public OtacException(String message, Throwable cause) {
        super(message, cause);
    }

    public OtacException(Throwable cause) {
        super(cause);
    }
}
