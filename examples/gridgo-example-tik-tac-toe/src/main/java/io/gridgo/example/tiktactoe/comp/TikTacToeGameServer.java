package io.gridgo.example.tiktactoe.comp;

import static io.gridgo.example.tiktactoe.TikTacToeConstants.ACTION;
import static io.gridgo.example.tiktactoe.TikTacToeConstants.CELL;
import static io.gridgo.example.tiktactoe.TikTacToeConstants.CMD;
import static io.gridgo.example.tiktactoe.TikTacToeConstants.FINISH;
import static io.gridgo.example.tiktactoe.TikTacToeConstants.GAME_ID;
import static io.gridgo.example.tiktactoe.TikTacToeConstants.MESSAGE;
import static io.gridgo.example.tiktactoe.TikTacToeConstants.NEW_GAME;
import static io.gridgo.example.tiktactoe.TikTacToeConstants.PLAYER_LIST;
import static io.gridgo.example.tiktactoe.TikTacToeConstants.ROUTING_ID;
import static io.gridgo.example.tiktactoe.TikTacToeConstants.TURN;
import static io.gridgo.example.tiktactoe.TikTacToeConstants.USERNAME;
import static io.gridgo.example.tiktactoe.TikTacToeConstants.WINNER;
import static io.gridgo.example.tiktactoe.TikTacToeConstants.WINNER_LINE;

import java.util.Collection;
import java.util.Map;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.example.tiktactoe.TikTacToeConstants;
import io.gridgo.example.tiktactoe.game.Game;
import io.gridgo.example.tiktactoe.game.GameEvent;
import io.gridgo.example.tiktactoe.game.GameEventType;
import io.gridgo.example.tiktactoe.game.GameException;
import io.gridgo.example.tiktactoe.game.GameManager;
import io.gridgo.example.tiktactoe.user.User;
import io.gridgo.example.tiktactoe.user.UserEventType;
import io.gridgo.example.tiktactoe.user.UserException;
import io.gridgo.example.tiktactoe.user.UserManager;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.Payload;
import io.gridgo.socket.SocketConstants;
import lombok.NonNull;

public class TikTacToeGameServer extends TikTacToeBaseComponent {

    private long userEventListenerId = -1;
    private final UserManager userManager = new UserManager();

    private long gameEventListenerId = -1;
    private final GameManager gameManager = new GameManager();

    public TikTacToeGameServer(String gatewayName) {
        super(gatewayName);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.userEventListenerId = this.userManager.addListener(this::onUserEvent);
        this.gameEventListenerId = this.gameManager.addListener(this::onGameEvent);
    }

    @Override
    protected void onStop() {
        this.userManager.removeListener(userEventListenerId);
        this.userEventListenerId = -1;

        this.gameManager.removeListener(gameEventListenerId);
        this.gameEventListenerId = -1;
    }

    @Override
    protected void processRequest(RoutingContext rc, GridgoContext gc) {
        try {
            Message message = rc.getMessage();
            Payload payload = message.getPayload();

            String routingId = payload.getHeaders().getString(TikTacToeConstants.ROUTING_ID, null);
            if (routingId != null) {
                String socketMessageType = payload.getHeaders().getString(SocketConstants.SOCKET_MESSAGE_TYPE, "");
                switch (socketMessageType.toLowerCase()) {
                case "open":
                    break;
                case "close":
                    this.userManager.removeUserBySession(routingId);
                    break;
                case "message":
                    this.handle(payload.getBody(), routingId);
                    break;
                default:
                }
            }
        } catch (Exception e) {
            getLogger().error("Error: ", e);
        }
    }

    private BObject generateMsgBody(String cmd) {
        return BObject.ofSequence(CMD, cmd);
    }

    private BObject generateError(String errMsg) {
        return generateMsgBody("error").setAny(MESSAGE, errMsg == null ? "Internal server error" : errMsg);
    }

    private void sendErrorToRoutingId(String routingId, String message) {
        this.sendToSessionId(routingId, generateError(message));
    }

    private void sendToSessionId(String routingId, BElement body) {
        this.getGateway().ifPresent(g -> g.get().send(Message.of(Payload.of(body).addHeader(ROUTING_ID, routingId))));
    }

    private void sendToUser(String userName, BElement body) {
        User user = this.userManager.getUser(userName);
        if (user != null) {
            this.sendToSessionId(user.getSessionId(), body);
        }
    }

    private void sendToAll(BElement msgBody) {
        Collection<User> allUser = this.userManager.getAllUser();
        for (User user : allUser) {
            sendToSessionId(user.getSessionId(), msgBody);
        }
    }

    private void sendToGame(long gameId, BElement msgBody) {
        Game game = gameManager.getGame(gameId);
        if (game != null) {
            for (String userName : game.getPlayerAssignedChar().keySet()) {
                this.sendToUser(userName, msgBody);
            }
        }
    }

    private void handle(BElement body, String routingId) {
        if (body != null) {
            if (body.isObject()) {
                BObject request = body.asObject();
                String cmd = request.getString(CMD);
                if (cmd != null) {
                    try {
                        switch (cmd.toLowerCase()) {
                        case "login":
                            String userName = request.getString("userName", "");
                            if (!userName.isBlank()) {
                                this.userManager.addUser(userName, routingId);
                            } else {
                                sendErrorToRoutingId(routingId, "userName is required on login");
                            }
                            break;
                        case "logout":
                            this.userManager.removeUser(routingId);
                            break;
                        case "game":
                            this.handleGameCommand(request.getObject("data", null), routingId);
                            break;
                        default:
                        }
                    } catch (UserException | GameException e) {
                        this.sendErrorToRoutingId(routingId, e.getMessage());
                    }
                }
            } else {
                sendErrorToRoutingId(routingId,
                        "Invalid request format, expected object (key-value), got " + body.getType());
            }
        } else {
            sendErrorToRoutingId(routingId, "Invalid request: null");
        }
    }

    private void handleGameCommand(BObject playData, String sessionId) {
        User user = this.userManager.getUserBySession(sessionId);
        if (user != null) {
            String userName = user.getUserName();
            if (playData != null) {
                String action = playData.getString(ACTION, null);
                if (action != null) {
                    switch (action.trim().toLowerCase()) {
                    case "create":
                        this.gameManager.createNewGame(userName);
                        break;
                    case "join":
                        long gameId = playData.getLong(GAME_ID, -1);
                        gameManager.addPlayerToGame(userName, gameId);
                        break;
                    case "quit":
                        this.gameManager.removePlayerFromGame(userName);
                        break;
                    case "move":
                        this.gameManager.handleMove(userName, playData.getInteger("x", -1),
                                playData.getInteger("y", -1));
                        break;
                    default:
                        throw new GameException("Invalid game action: " + action);
                    }
                } else {
                    throw new GameException("Game action is required");
                }
            } else {
                throw new GameException("Invalid game request");
            }
        } else {
            throw new UserException("User not found, plz login first");
        }
    }

    private void onUserEvent(UserEventType event, User user) {
        switch (event) {
        case LOGGED_IN:
            onUserLoggedIn(user);
            break;
        case LOGGED_OUT:
            this.gameManager.removePlayerFromGame(user.getUserName());
            break;
        default:
            break;
        }
    }

    private void onUserLoggedIn(User user) {
        getLogger().debug("User logged in: " + user.getUserName() + " with sessionId: " + user.getSessionId());
        Collection<Game> games = gameManager.getGames();
        BArray gameList = BArray.ofEmpty();
        for (Game game : games) {
            gameList.add(BObject.ofEmpty() //
                                .setAny(GAME_ID, game.getId()) //
                                .setAny(PLAYER_LIST, game.getPlayerAssignedChar()) //
            );
        }
        BObject msgBody = generateMsgBody("loggedIn")//
                                                     .setAny(TikTacToeConstants.USERNAME, user.getUserName()) //
                                                     .setAny(TikTacToeConstants.GAME_LIST, gameList);

        this.sendToSessionId(user.getSessionId(), msgBody);
    }

    private void onGameEvent(@NonNull GameEvent event) {
        GameEventType type = event.getType();
        switch (type) {
        case PLAYER_JOIN:
            this.onPlayerJoinGame(event.getGame(), event.getPlayer(), event.isCreator());
            break;
        case PLAYER_QUIT:
            this.onPlayerQuitGame(event.getGame(), event.getPlayer());
            break;
        case PLAYER_MOVE:
            this.onPlayerMove(event.getGame(), event.getPlayer(), event.getX(), event.getY(), event.isFinish(),
                    event.getWinner(), event.getWinnerLine());
            break;
        case GAME_DELETED:
            this.onGameDeleted(event.getGame());
        default:
        }
    }

    private void onGameDeleted(Game game) {
        this.sendToAll(this.generateMsgBody("gameDeleted") //
                           .setAny(GAME_ID, game.getId()));
    }

    private void onPlayerJoinGame(Game game, String player, boolean isNewGame) {
        Map<String, Character> playerList = game.getPlayerAssignedChar();
        BObject messageBody = this.generateMsgBody("playerJoinGame") //
                                  .setAny(USERNAME, player) //
                                  .setAny(NEW_GAME, isNewGame) //
                                  .setAny(GAME_ID, game.getId()) //
                                  .setAny(PLAYER_LIST, playerList);

        this.sendToAll(messageBody);

        if (game.getPlayerAssignedChar().size() > 1) {
            this.sendToGame(game.getId(), generateMsgBody("startGame") //
                                                                      .setAny(PLAYER_LIST, game.getPlayerAssignedChar()) //
                                                                      .setAny(TURN, game.getTurn()) //
            );
        }
    }

    private void onPlayerQuitGame(Game game, String player) {
        Map<String, Character> playerList = game.getPlayerAssignedChar();
        BObject messageBody = this.generateMsgBody("playerQuitGame") //
                                  .setAny(USERNAME, player) //
                                  .setAny(GAME_ID, game.getId()) //
                                  .setAny(PLAYER_LIST, playerList);

        this.sendToAll(messageBody);
        this.sendToGame(game.getId(), generateMsgBody("clearGame") //
                                                                  .setAny(PLAYER_LIST, playerList) //
        );
    }

    private void onPlayerMove(Game game, String player, int x, int y, boolean finish, String winner,
            int[][] winnerLine) {
        Map<String, Character> playerList = game.getPlayerAssignedChar();
        BObject msgBody = this.generateMsgBody("playerMove") //
                              .setAny(USERNAME, player) //
                              .setAny(GAME_ID, game.getId()) //
                              .setAny(FINISH, finish) //
                              .setAny(CELL, BObject.ofSequence("x", x, "y", y, "char", playerList.get(player))) //
                              .setAny(PLAYER_LIST, playerList);

        if (finish) {
            msgBody //
                   .setAny(WINNER, winner) //
                   .setAny(WINNER_LINE, winnerLine);

            game.reset();
        }

        msgBody.setAny(TikTacToeConstants.TURN, game.getTurn());

        this.sendToGame(game.getId(), msgBody);
    }
}
