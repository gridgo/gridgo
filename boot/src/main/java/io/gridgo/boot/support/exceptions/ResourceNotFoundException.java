package io.gridgo.boot.support.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -540767983285741746L;

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
