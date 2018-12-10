package io.gridgo.example.tiktactoe.game;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameEvent {

    private GameEventType type;
    private Game game;

    private boolean creator;

    private String player;
    private int x;
    private int y;

    private boolean finish;
    private int[][] winnerLine;
    private String winner;
}
