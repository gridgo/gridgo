package io.gridgo.example.tiktactoe.game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import io.gridgo.utils.helper.Loggable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
public class Game implements Loggable {

    @Getter
    private final long id;
    private final List<Character> availableChars = new LinkedList<>();
    private final Map<String, Character> playerToAssignedChar = new HashMap<>();

    private final char[][] board = new char[3][];
    private final AtomicReference<String> winner = new AtomicReference<String>(null);
    private final AtomicReference<int[][]> winnerLine = new AtomicReference<int[][]>(null);

    private final AtomicReference<String> turn = new AtomicReference<String>(null);

    {
        this.availableChars.add('x');
        this.availableChars.add('o');
        this.reset();
    }

    public void reset() {
        if (winner.get() != null) {
            this.turn.set(winner.get());
        }

        this.winner.set(null);
        this.winnerLine.set(null);
        for (int i = 0; i < 3; i++) {
            this.board[i] = new char[] { 0, 0, 0 };
        }
    }

    public String getTurn() {
        return this.turn.get();
    }

    public String getNonTurn() {
        if (this.playerToAssignedChar.size() > 1 && this.turn.get() != null) {
            for (String nonTurn : this.playerToAssignedChar.keySet()) {
                if (!nonTurn.equals(turn.get())) {
                    return nonTurn;
                }
            }
        }
        return null;
    }

    public String getWinner() {
        return this.winner.get();
    }

    public int[][] getWinnerLine() {
        return this.winnerLine.get();
    }

    public String getPlayerForChar(char assignedChar) {
        for (Entry<String, Character> entry : this.playerToAssignedChar.entrySet()) {
            if (entry.getValue().charValue() == assignedChar) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Map<String, Character> getPlayerAssignedChar() {
        Map<String, Character> result = new HashMap<>();
        synchronized (this.playerToAssignedChar) {
            for (Entry<String, Character> entry : this.playerToAssignedChar.entrySet()) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public char[][] getBoard() {
        char[][] result = new char[3][];
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                result[y][x] = this.board[y][x];
            }
        }
        return result;
    }

    public void clear() {
        Set<String> players = new HashSet<>(this.playerToAssignedChar.keySet());
        for (String player : players) {
            this.removePlayer(player);
        }
    }

    public void addPlayer(String player) {
        GameException ex = null;
        synchronized (playerToAssignedChar) {
            if (this.playerToAssignedChar.size() < 2) {
                this.playerToAssignedChar.put(player, availableChars.remove(0));
                if (this.playerToAssignedChar.size() > 1 && getTurn() == null) {
                    for (String p : this.playerToAssignedChar.keySet()) {
                        if (!p.equals(player)) {
                            this.turn.set(p);
                            break;
                        }
                    }
                }
            } else {
                ex = new GameException("Game is full");
            }
        }

        if (ex != null) {
            throw ex;
        }
    }

    public boolean removePlayer(String player) {
        synchronized (playerToAssignedChar) {
            Character assignedChar = this.playerToAssignedChar.remove(player);
            if (assignedChar != null) {
                this.turn.set(null);
                this.availableChars.add(assignedChar);
                this.reset();
                return true;
            }
        }
        return false;
    }

    public boolean move(@NonNull String player, int x, int y) {
        if (this.getPlayerAssignedChar().size() < 2) {
            throw new GameException("Invalid state, player not enough");
        }
        getLogger().debug("player {} try to move while turn={} ", player, this.turn.get());
        Character assignedChar = this.playerToAssignedChar.get(player);
        if (assignedChar != null) {
            if (x >= 0 && x <= 3 && y >= 0 && y <= 3) {
                char cellValue = this.board[y][x];
                if (cellValue == 0) {
                    if (this.turn.compareAndSet(player, this.getNonTurn())) {
                        this.board[y][x] = assignedChar.charValue();
                        return this.checkFinishGame();
                    } else {
                        throw new GameException("Bad move, invalid turn");
                    }
                } else {
                    throw new GameException("Bad move, cell already filled");
                }
            } else {
                throw new GameException("Bad move, invalid cell location (" + x + "," + y + ")");
            }
        } else {
            throw new GameException("Player not in game");
        }
    }

    private char check3Chars(char[] cells) {
        char firstChar = 0;
        for (char cell : cells) {
            if (cell == 0) {
                return 0;
            } else if (firstChar == 0) {
                firstChar = cell;
            } else if (firstChar != cell) {
                return 0;
            }
        }
        return firstChar;
    }

    private boolean checkFinishGame() {
        char winChar = 0;

        /**
         * check rows
         */
        for (int y = 0; y < 3; y++) {
            winChar = check3Chars(this.board[y]);
            if (winChar != 0) {
                if (this.winner.compareAndSet(null, this.getPlayerForChar(winChar))) {
                    winnerLine.set(new int[][] { new int[] { y, 0 }, new int[] { y, 1 }, new int[] { y, 2 } });
                    return true;
                }
                throw new GameException("Cannot mark player as winner");
            }
        }

        /**
         * check columns
         */
        for (int x = 0; x < 3; x++) {
            char[] column = new char[] { this.board[0][x], this.board[1][x], this.board[2][x] };
            winChar = check3Chars(column);
            if (winChar != 0) {
                if (this.winner.compareAndSet(null, this.getPlayerForChar(winChar))) {
                    winnerLine.set(new int[][] { new int[] { 0, x }, new int[] { 1, x }, new int[] { 2, x } });
                    return true;
                }
                throw new GameException("Cannot mark player as winner");
            }
        }

        /**
         * check diagonals
         */
        // forward diagonal
        char[] forwardDiagonal = new char[] { this.board[0][0], this.board[1][1], this.board[2][2] };
        if ((winChar = check3Chars(forwardDiagonal)) != 0) {
            if (this.winner.compareAndSet(null, this.getPlayerForChar(winChar))) {
                winnerLine.set(new int[][] { new int[] { 0, 0 }, new int[] { 1, 1 }, new int[] { 2, 2 } });
                return true;
            }
            throw new GameException("Invalid game state, cannot mark player as winner");
        }
        // backward diagonal
        char[] backwardDiagonal = new char[] { this.board[0][2], this.board[1][1], this.board[2][0] };
        if ((winChar = check3Chars(backwardDiagonal)) != 0) {
            if (this.winner.compareAndSet(null, this.getPlayerForChar(winChar))) {
                winnerLine.set(new int[][] { new int[] { 0, 2 }, new int[] { 1, 1 }, new int[] { 2, 0 } });
                return true;
            }
            throw new GameException("Invalid game state, cannot mark player as winner");
        }

        int filledCount = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (this.board[y][x] != 0) {
                    filledCount++;
                }
            }
        }

        return filledCount == 9;
    }

}
