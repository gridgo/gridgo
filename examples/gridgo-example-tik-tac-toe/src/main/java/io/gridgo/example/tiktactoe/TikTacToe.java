package io.gridgo.example.tiktactoe;

import io.gridgo.core.GridgoContext;
import io.gridgo.core.impl.DefaultGridgoContextBuilder;
import io.gridgo.example.tiktactoe.comp.TikTacToeGameServer;
import io.gridgo.example.tiktactoe.comp.TikTacToeWebSocket;
import io.gridgo.example.tiktactoe.comp.TikTacToeWebserver;
import io.gridgo.framework.NonameComponentLifecycle;

public class TikTacToe extends NonameComponentLifecycle {

	private static final String GATEWAY_WEBSERVER = "tikTacToeWebserver";

	private static final String GATEWAY_WEBSOCKET = "tikTacToeWebSocket";

	private static final String GATEWAY_GAMESERVER = "tikTacToeGameServer";

	private final GridgoContext appContext;

	public TikTacToe() {
		this.appContext = new DefaultGridgoContextBuilder().setName("TikTacToe").build();

		this.appContext //
				.attachComponent(new TikTacToeWebserver(GATEWAY_WEBSERVER)) //
				.attachComponent(new TikTacToeWebSocket(GATEWAY_WEBSOCKET)) //
				.attachComponent(new TikTacToeGameServer(GATEWAY_GAMESERVER));

		appContext.openGateway(GATEWAY_WEBSERVER) //
				.attachConnector("jetty:http://localhost:8888/*");

		appContext.openGateway(GATEWAY_WEBSOCKET) //
				.attachConnector("netty4:server:ws://localhost:8889/tiktactoe") //
				.attachConnector("zmq:push:ipc://clientToGame") //
				.attachConnector("zmq:pull:ipc://gameToClient");

		appContext.openGateway(GATEWAY_GAMESERVER) //
				.attachConnector("zmq:pull:ipc://clientToGame") //
				.attachConnector("zmq:push:ipc://gameToClient");
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
