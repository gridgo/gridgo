package io.gridgo.example.tiktactoe;

import static io.gridgo.example.tiktactoe.TikTacToeConstants.ROUTING_ID;
import static io.gridgo.socket.SocketConstants.SOCKET_MESSAGE_TYPE;

import io.gridgo.bean.BValue;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.impl.DefaultGridgoContextBuilder;
import io.gridgo.core.support.impl.BridgeComponent;
import io.gridgo.example.tiktactoe.comp.TikTacToeGameServer;
import io.gridgo.example.tiktactoe.comp.TikTacToeHttp;
import io.gridgo.framework.NonameComponentLifecycle;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.Payload;

public class TikTacToe extends NonameComponentLifecycle {

	private static final String GATEWAY_HTTP = "tikTacToeWebserver";

	private static final String GATEWAY_WEBSOCKET = "tikTacToeWebSocket";

	private static final String GATEWAY_GAMESERVER = "tikTacToeGameServer";

	private static final String GATEWAY_WS_TO_GAME = "tikTacToeWsToGame";

	private final GridgoContext appContext;

	public TikTacToe() {
		this.appContext = new DefaultGridgoContextBuilder().setName("TikTacToe").setExceptionHandler(this::onException)
				.build();

		appContext.openGateway(GATEWAY_HTTP) //
				.attachConnector("jetty:http://localhost:8888/[tiktactoe/*]");

		appContext.openGateway(GATEWAY_WEBSOCKET) //
				.attachConnector("netty4:server:ws://localhost:8889/tiktactoe");

		appContext.openGateway(GATEWAY_WS_TO_GAME) //
				.attachConnector("zmq:push:ipc://clientToGame") //
				.attachConnector("zmq:pull:ipc://gameToClient");

		appContext.openGateway(GATEWAY_GAMESERVER) //
				.attachConnector("zmq:pull:ipc://clientToGame") //
				.attachConnector("zmq:push:ipc://gameToClient");

		this.appContext //
				// handle http request
				.attachComponent(new TikTacToeHttp(GATEWAY_HTTP)) //
				// handle game logic request
				.attachComponent(new TikTacToeGameServer(GATEWAY_GAMESERVER)) //
				// attach 2 bridge components to fwd msg from/to websocket to/from game via zmq
				.attachComponent(new BridgeComponent(GATEWAY_WEBSOCKET, GATEWAY_WS_TO_GAME, this::forwardMsgWsToGame)) //
				.attachComponent(new BridgeComponent(GATEWAY_WS_TO_GAME, GATEWAY_WEBSOCKET, this::forwardMsgGameToWs)) //
		;
	}

	private void onException(Throwable ex) {
		getLogger().error("Internal error", ex);
	}

	private Message forwardMsgWsToGame(Message input) {
		Payload payload = input.getPayload();
		if (payload == null) {
			payload = Payload.of(null);
			input.setPayload(payload);
		}
		payload.addHeader(SOCKET_MESSAGE_TYPE, input.getMisc().get(SOCKET_MESSAGE_TYPE));
		payload.addHeader(ROUTING_ID, input.getRoutingId().orElse(BValue.ofEmpty()).getData());
		return input;
	}

	private Message forwardMsgGameToWs(Message input) {
		input.setRoutingId(input.getPayload().getHeaders().getValue(ROUTING_ID));
		input.getPayload().getHeaders().remove(ROUTING_ID);
		return input;
	}

	@Override
	protected void onStart() {
		this.appContext.start();
	}

	@Override
	protected void onStop() {
		this.appContext.stop();
	}
}
