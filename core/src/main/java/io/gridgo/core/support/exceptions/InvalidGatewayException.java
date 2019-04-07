package io.gridgo.core.support.exceptions;

public class InvalidGatewayException extends RuntimeException {

    private static final long serialVersionUID = 6587345312524341279L;

    public InvalidGatewayException(String name) {
        super("Invalid gateway: " + name);
    }
}
