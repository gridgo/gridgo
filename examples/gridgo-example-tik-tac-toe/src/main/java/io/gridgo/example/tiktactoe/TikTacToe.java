package io.gridgo.example.tiktactoe;

import io.gridgo.core.GridgoContext;
import io.gridgo.core.impl.DefaultGridgoContextBuilder;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.example.tiktactoe.comp.TikTacToeGameServer;
import io.gridgo.example.tiktactoe.comp.TikTacToeWebSocket;
import io.gridgo.example.tiktactoe.comp.TikTacToeWebserver;
import io.gridgo.framework.NonameComponentLifecycle;

public class TikTacToe extends NonameComponentLifecycle {

	private final GridgoContext appContext;

	private GatewaySubscription websocketGateway;
	private GatewaySubscription webserverGateway;
	private GatewaySubscription gameServerGateway;

	public TikTacToe() {
		this.appContext = new DefaultGridgoContextBuilder().setName("TikTacToe").build();

		String webServerGatewayName = "tikTacToeWebserver";
		String webSocketGatewayName = "tikTacToeWebSocket";
		String gameServerGatewayName = "tikTacToeGameServer";

		this.appContext //
				.attachComponent(new TikTacToeWebserver(webServerGatewayName)) //
				.attachComponent(new TikTacToeWebSocket(webSocketGatewayName)) //
				.attachComponent(new TikTacToeGameServer(gameServerGatewayName));

		webserverGateway = appContext.openGateway(webServerGatewayName);
		webserverGateway.attachConnector("jetty:http://localhost:8888/*");

		websocketGateway = appContext.openGateway(webServerGatewayName);
		websocketGateway.attachConnector("netty4:server:ws://localhost:8889/tiktactoe");
		websocketGateway.attachConnector("zmq:push:ipc://clientToGame");
		websocketGateway.attachConnector("zmq:pull:ipc://gameToClient");

		gameServerGateway = appContext.openGateway(gameServerGatewayName);
		gameServerGateway.attachConnector("zmq:pull:ipc://clientToGame");
		gameServerGateway.attachConnector("zmq:push:ipc://gameToClient");
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
