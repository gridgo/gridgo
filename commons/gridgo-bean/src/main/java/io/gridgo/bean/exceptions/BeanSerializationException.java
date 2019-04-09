package io.gridgo.bean.exceptions;

public class BeanSerializationException extends RuntimeException {

    private static final long serialVersionUID = -6842634768745213107L;

    public BeanSerializationException(String msg, Throwable e) {
        super(msg, e);
    }

    public BeanSerializationException(String msg) {
        super(msg);
    }
}
