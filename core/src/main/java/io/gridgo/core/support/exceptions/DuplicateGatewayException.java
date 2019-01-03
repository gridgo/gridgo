package io.gridgo.core.support.exceptions;

public class DuplicateGatewayException extends RuntimeException {

    private static final long serialVersionUID = 6587345312524341279L;

    public DuplicateGatewayException(String name) {
        super("Gateway already opened: " + name);
    }
}
