package io.gridgo.connector.support.exceptions;

public class ConnectorResolutionException extends RuntimeException {

    private static final long serialVersionUID = -258053720079914880L;

    public ConnectorResolutionException(String msg, Exception cause) {
        super(msg, cause);
    }
}
