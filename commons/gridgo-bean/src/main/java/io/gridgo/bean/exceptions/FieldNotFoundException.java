package io.gridgo.bean.exceptions;

public class FieldNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 6149795637073025447L;

    public FieldNotFoundException(String message) {
        super(message);
    }
}
