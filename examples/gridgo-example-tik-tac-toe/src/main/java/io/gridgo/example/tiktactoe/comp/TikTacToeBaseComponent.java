package io.gridgo.example.tiktactoe.comp;

import io.gridgo.core.Gateway;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.ContextAwareComponent;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.framework.NonameComponentLifecycle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

abstract class TikTacToeBaseComponent extends NonameComponentLifecycle implements ContextAwareComponent {

	@Setter
	@Getter(AccessLevel.PROTECTED)
	private GridgoContext context;

	private final String gatewayName;

	protected TikTacToeBaseComponent(String gatewayName) {
		this.gatewayName = gatewayName;
	}

	@Override
	protected final void onStart() {
		this.getGateway().subscribe(this::processRequest);
	}

	@Override
	protected void onStop() {
		// do nothing...
	}

	protected Gateway getGateway() {
		return this.getContext().findGateway(gatewayName).orElse(null);
	}

	protected abstract void processRequest(RoutingContext rc, GridgoContext gc);
}
