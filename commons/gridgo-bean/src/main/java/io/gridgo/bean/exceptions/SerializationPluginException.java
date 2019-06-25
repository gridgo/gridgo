package io.gridgo.bean.exceptions;

public class SerializationPluginException extends RuntimeException {

    private static final long serialVersionUID = -5630203172743701283L;

    public SerializationPluginException() {
        super();
    }

    public SerializationPluginException(String message) {
        super(message);
    }

    public SerializationPluginException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializationPluginException(Throwable cause) {
        super(cause);
    }
}
