package io.gridgo.example.tiktactoe.comp;

import java.util.Optional;

import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.ContextAwareComponent;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.core.support.subscription.GatewaySubscription;
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
    protected void onStart() {
        this.getGateway().ifPresent(g -> g.subscribe(this::processRequest));
    }

    @Override
    protected void onStop() {
        // do nothing...
    }

    protected Optional<GatewaySubscription> getGateway() {
        return this.getContext().getGatewaySubscription(gatewayName);
    }

    protected abstract void processRequest(RoutingContext rc, GridgoContext gc);
}
