package io.gridgo.example.tiktactoe.comp;

import static io.gridgo.example.tiktactoe.TikTacToeConstants.ACTION;
import static io.gridgo.example.tiktactoe.TikTacToeConstants.CELL;
import static io.gridgo.example.tiktactoe.TikTacToeConstants.CMD;
import static io.gridgo.example.tiktactoe.TikTacToeConstants.CREATOR;
import static io.gridgo.example.tiktactoe.TikTacToeConstants.FINISH;
import static io.gridgo.example.tiktactoe.TikTacToeConstants.GAME_ID;
import static io.gridgo.example.tiktactoe.TikTacToeConstants.MESSAGE;
import static io.gridgo.example.tiktactoe.TikTacToeConstants.PLAYER_LIST;
import static io.gridgo.example.tiktactoe.TikTacToeConstants.ROUTING_ID;
import static io.gridgo.example.tiktactoe.TikTacToeConstants.USERNAME;

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
		Message message = rc.getMessage();
		Payload payload = message.getPayload();

		long routingId = payload.getHeaders().getLong(TikTacToeConstants.ROUTING_ID, -1);
		if (routingId >= 0) {
			String socketMessageType = payload.getHeaders().getString(SocketConstants.SOCKET_MESSAGE_TYPE, "");
			switch (socketMessageType.toLowerCase()) {
			case "open":
				getLogger().info("New session: " + routingId);
				break;
			case "close":
				this.userManager.removeUser(routingId);
				break;
			case "message":
				this.handle(payload.getBody(), routingId);
				break;
			}
		}
	}

	private BObject generateMsgBody(String cmd) {
		return BObject.ofSequence(CMD, cmd);
	}

	private BObject generateError(String errMsg) {
		return generateMsgBody("error").setAny(MESSAGE, errMsg == null ? "Internal server error" : errMsg);
	}

	private void sendError(long routingId, String message) {
		this.send(routingId, generateError(message));
	}

	private void send(long routingId, BElement body) {
		this.getGateway().send(Message.of(Payload.of(body).addHeader(ROUTING_ID, routingId)));
	}

	private void send(String userName, BElement body) {
		User user = this.userManager.getUser(userName);
		if (user != null) {
			this.send(user.getSessionId(), body);
		}
	}

	private void sendToAll(BElement msgBody) {
		Collection<User> allUser = this.userManager.getAllUser();
		for (User user : allUser) {
			send(user.getSessionId(), msgBody);
		}
	}

	private void handle(BElement body, long routingId) {
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
								sendError(routingId, "userName is required on login");
							}
							break;
						case "logout":
							this.userManager.removeUser(routingId);
							break;
						case "game":
							this.handleGameCommand(request.getObject("data", null), routingId);
							break;
						}
					} catch (UserException | GameException e) {
						this.sendError(routingId, e.getMessage());
					}
				}
			} else {
				sendError(routingId, "Invalid request format, expected object (key-value), got " + body.getType());
			}
		} else {
			sendError(routingId, "Invalid request: null");
		}
	}

	private void handleGameCommand(BObject playData, long routingId) {
		User user = this.userManager.getUser(routingId);
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
					.setAny(PLAYER_LIST, game.getPlayerAssignedChar().keySet()) //
			);
		}
		BObject msgBody = generateMsgBody("loggedIn")//
				.setAny(TikTacToeConstants.USERNAME, user.getUserName()) //
				.setAny(TikTacToeConstants.GAME_LIST, gameList);

		this.send(user.getSessionId(), msgBody);
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
			// do nothing
			break;
		}
	}

	private void onGameDeleted(Game game) {
		this.sendToAll(this.generateMsgBody("gameDeleted") //
				.setAny(GAME_ID, game.getId()));
	}

	private void onPlayerJoinGame(Game game, String player, boolean creator) {
		Map<String, Character> playerList = game.getPlayerAssignedChar();
		BObject messageBody = this.generateMsgBody("playerJoinGame") //
				.setAny(USERNAME, player) //
				.setAny(CREATOR, creator) //
				.setAny(GAME_ID, game.getId()) //
				.setAny(PLAYER_LIST, playerList);

		for (String userName : playerList.keySet()) {
			this.send(userName, messageBody);
		}

		if (creator) {
			sendToAll(generateMsgBody("gameAdded") //
					.setAny(GAME_ID, game.getId()) //
					.setAny(PLAYER_LIST, playerList));
		}
	}

	private void onPlayerQuitGame(Game game, String player) {
		Map<String, Character> playerList = game.getPlayerAssignedChar();
		BObject messageBody = this.generateMsgBody("playerQuitGame") //
				.setAny(USERNAME, player) //
				.setAny(GAME_ID, game.getId()) //
				.setAny(PLAYER_LIST, playerList);

		for (String userName : playerList.keySet()) {
			this.send(userName, messageBody);
		}
	}

	private void onPlayerMove(Game game, String player, int x, int y, boolean finish, String winner,
			int[][] winnerLine) {
		Map<String, Character> playerList = game.getPlayerAssignedChar();
		BObject messageBody = this.generateMsgBody("playerMove") //
				.setAny(USERNAME, player) //
				.setAny(GAME_ID, game.getId()) //
				.setAny(FINISH, finish) //
				.setAny(CELL, BObject.ofSequence("x", x, "y", y, "char", playerList.get(player))) //
				.setAny(PLAYER_LIST, playerList);

		if (finish) {
			messageBody //
					.setAny(TikTacToeConstants.WINNER, winner) //
					.setAny(TikTacToeConstants.WINNER_LINE, winnerLine);
		}
		for (String userName : playerList.keySet()) {
			this.send(userName, messageBody);
		}
	}
}
