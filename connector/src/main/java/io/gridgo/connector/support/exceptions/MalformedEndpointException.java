package io.gridgo.connector.support.exceptions;

public class MalformedEndpointException extends RuntimeException {

    private static final long serialVersionUID = 3124330489055840260L;

    public MalformedEndpointException(String msg) {
        super(msg);
    }
}
