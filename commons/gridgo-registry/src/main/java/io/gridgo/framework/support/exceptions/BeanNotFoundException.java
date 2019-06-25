package io.gridgo.framework.support.exceptions;

public class BeanNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -8383253748791319830L;

    public BeanNotFoundException(String msg) {
        super(msg);
    }
}
