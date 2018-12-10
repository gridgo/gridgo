package io.gridgo.example.tiktactoe.user;

public class UserException extends RuntimeException {

    private static final long serialVersionUID = -2278086054256519604L;

    public UserException(String message) {
        super(message);
    }
}
