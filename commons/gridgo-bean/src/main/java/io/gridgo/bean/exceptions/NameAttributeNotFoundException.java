package io.gridgo.bean.exceptions;

public class NameAttributeNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1576517171435681010L;

    public NameAttributeNotFoundException(String message) {
        super(message);
    }
}
