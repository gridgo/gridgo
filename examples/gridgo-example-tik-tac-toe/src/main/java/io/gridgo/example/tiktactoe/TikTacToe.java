package io.gridgo.example.tiktactoe;

import io.gridgo.core.GridgoContext;
import io.gridgo.core.impl.DefaultGridgoContextBuilder;
import io.gridgo.example.tiktactoe.comp.TikTacToeGameServer;
import io.gridgo.example.tiktactoe.comp.TikTacToeWebSocket;
import io.gridgo.example.tiktactoe.comp.TikTacToeWebserver;
import io.gridgo.framework.NonameComponentLifecycle;

public class TikTacToe extends NonameComponentLifecycle {

	private final GridgoContext appContext;

	public TikTacToe() {
		this.appContext = new DefaultGridgoContextBuilder().setName("TikTacToe").build();

		String webServerGatewayName = "tikTacToeWebserver";
		String webSocketGatewayName = "tikTacToeWebSocket";
		String gameServerGatewayName = "tikTacToeGameServer";

		this.appContext //
				.attachComponent(new TikTacToeWebserver(webServerGatewayName)) //
				.attachComponent(new TikTacToeWebSocket(webSocketGatewayName)) //
				.attachComponent(new TikTacToeGameServer(gameServerGatewayName));

		appContext.openGateway(webServerGatewayName) //
				.attachConnector("jetty:http://localhost:8888/*");

		appContext.openGateway(webSocketGatewayName) //
				.attachConnector("netty4:server:ws://localhost:8889/tiktactoe") //
				.attachConnector("zmq:push:ipc://clientToGame") //
				.attachConnector("zmq:pull:ipc://gameToClient");

		appContext.openGateway(gameServerGatewayName) //
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
