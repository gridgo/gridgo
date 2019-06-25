package io.gridgo.connector.support.exceptions;

public class SendMessageException extends Exception {

    private static final long serialVersionUID = -248588838925175405L;

    public SendMessageException() {
        super();
    }

    public SendMessageException(Exception e) {
        super(e);
    }
}
