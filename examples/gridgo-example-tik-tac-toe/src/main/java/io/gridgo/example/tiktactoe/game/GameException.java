package io.gridgo.example.tiktactoe.game;

public class GameException extends RuntimeException {

    private static final long serialVersionUID = 1445754419343675282L;

    public GameException(String message) {
        super(message);
    }
}
