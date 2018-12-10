package io.gridgo.example.tiktactoe.game;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class GameManager {

    private final AtomicLong gameIdSeed = new AtomicLong(0);
    private final Map<Long, Game> games = new NonBlockingHashMap<>();

    private final AtomicLong listenerIdSeed = new AtomicLong(0);
    private final Map<Long, Consumer<GameEvent>> listeners = new NonBlockingHashMap<>();

    private final Map<String, Game> userToGameLookUpMap = new NonBlockingHashMap<>();

    private void dispatchEvent(GameEvent event) {
        this.listeners.values().forEach(listener -> {
            listener.accept(event);
        });
    }

    public long addListener(Consumer<GameEvent> listener) {
        long id = listenerIdSeed.getAndIncrement();
        this.listeners.put(id, listener);
        return id;
    }

    public void removeListener(long listenerId) {
        this.listeners.remove(listenerId);
    }

    public Game lookUpGameForPlayer(String player) {
        return this.userToGameLookUpMap.get(player);
    }

    public void createNewGame(String creator) {
        Game currentGame = this.lookUpGameForPlayer(creator);
        if (currentGame == null) {
            long id = gameIdSeed.getAndIncrement();
            Game game = new Game(id);
            this.games.put(id, game);
            this.addPlayerToGame(creator, id, true);
        } else {
            throw new GameException("User " + creator + " is already in other game");
        }
    }

    public void addPlayerToGame(String player, long gameId) {
        this.addPlayerToGame(player, gameId, false);
    }

    private void addPlayerToGame(String player, long gameId, boolean isCreator) {
        Game currentGame = this.userToGameLookUpMap.get(player);
        if (currentGame == null) {
            Game game = this.games.get(gameId);
            if (game != null) {
                game.addPlayer(player);
                this.userToGameLookUpMap.put(player, game);
                this.dispatchEvent(GameEvent.builder() //
                                            .type(GameEventType.PLAYER_JOIN) //
                                            .player(player) //
                                            .game(game) //
                                            .creator(isCreator) //
                                            .build());
            }
        } else {
            throw new GameException("User already in game");
        }
    }

    public void removePlayerFromGame(String player) {
        Game currentGame = this.lookUpGameForPlayer(player);
        if (currentGame != null) {
            this.userToGameLookUpMap.remove(player);
            if (currentGame.removePlayer(player)) {
                this.dispatchEvent(GameEvent.builder() //
                                            .type(GameEventType.PLAYER_QUIT) //
                                            .player(player) //
                                            .game(currentGame) //
                                            .build());
            }

            if (currentGame.getPlayerAssignedChar().size() == 0) {
                this.deleteGame(currentGame.getId());
            }
        }
    }

    private void deleteGame(long id) {
        Game game = this.games.remove(id);
        if (game != null) {
            this.dispatchEvent(GameEvent.builder() //
                                        .type(GameEventType.GAME_DELETED) //
                                        .game(game) //
                                        .build());
        }
    }

    public void handleMove(String player, int x, int y) {
        Game game = this.lookUpGameForPlayer(player);
        if (game != null) {
            GameEvent.GameEventBuilder builder = GameEvent.builder() //
                                                          .type(GameEventType.PLAYER_MOVE) //
                                                          .game(game) //
                                                          .player(player) //
                                                          .x(x) //
                                                          .y(y);

            boolean isFinish = game.move(player, x, y);
            if (isFinish) {
                builder.finish(true).winner(game.getWinner()).winnerLine(game.getWinnerLine());
            }
            this.dispatchEvent(builder.build());
            return;
        }
        throw new GameException("Player wasn't in game");
    }

    public Collection<Game> getGames() {
        return this.games.values();
    }

    public Game getGame(long gameId) {
        return this.games.get(gameId);
    }

}
