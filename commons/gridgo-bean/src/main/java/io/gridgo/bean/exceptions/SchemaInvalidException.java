package io.gridgo.bean.exceptions;

public class SchemaInvalidException extends RuntimeException {

    private static final long serialVersionUID = -4672017034231541252L;

    public SchemaInvalidException(String message) {
        super(message);
    }
}
