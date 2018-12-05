package io.gridgo.example.tiktactoe.comp;

import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.RoutingContext;

public class TikTacToeWebSocket extends TikTacToeBaseComponent {

	public TikTacToeWebSocket(String gatewayName) {
		super(gatewayName);
	}

	@Override
	protected void processRequest(RoutingContext rc, GridgoContext gc) {

	}
}
