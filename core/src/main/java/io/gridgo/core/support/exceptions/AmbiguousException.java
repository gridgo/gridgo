package io.gridgo.core.support.exceptions;

public class AmbiguousException extends RuntimeException {

    private static final long serialVersionUID = -6395059645583328336L;

    public AmbiguousException(String msg) {
        super(msg);
    }
}
