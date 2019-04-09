package io.gridgo.connector.support.exceptions;

public class UnsupportedSchemeException extends RuntimeException {

    private static final long serialVersionUID = 3124330489055840260L;

    public UnsupportedSchemeException(String scheme) {
        super("Unsupported scheme: " + scheme);
    }
}
