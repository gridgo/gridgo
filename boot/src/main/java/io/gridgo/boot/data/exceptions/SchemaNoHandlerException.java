package io.gridgo.boot.data.exceptions;

public class SchemaNoHandlerException extends RuntimeException {

    private static final long serialVersionUID = 3934730170738820101L;

    public SchemaNoHandlerException(String msg) {
        super(msg);
    }
}
