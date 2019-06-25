package io.gridgo.utils.exception;

import java.io.IOException;

public class RuntimeIOException extends RuntimeException {

    private static final long serialVersionUID = 3464412024532717387L;

    public RuntimeIOException(IOException e) {
        super(e);
    }

    public RuntimeIOException(String message, IOException e) {
        super(message, e);
    }

}
