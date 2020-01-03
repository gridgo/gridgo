package io.gridgo.core.support.config;

import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.config.ConfiguratorNode.ComponentNode;
import io.gridgo.core.support.config.ConfiguratorNode.ConnectorNode;
import io.gridgo.core.support.config.ConfiguratorNode.GatewayNode;
import io.gridgo.core.support.config.ConfiguratorNode.InstrumenterNode;
import io.gridgo.core.support.config.ConfiguratorNode.RootNode;
import io.gridgo.core.support.config.ConfiguratorNode.SubscriberNode;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.core.support.subscription.ProcessorSubscription;

public interface ContextConfiguratorVisitor {

    public GridgoContext visit(RootNode rootContext);

    public void visit(GridgoContext gc, ComponentNode ctx);

    public void visit(GridgoContext gc, GatewayNode ctx);

    public void visit(GatewaySubscription sub, SubscriberNode ctx);

    public void visit(ProcessorSubscription procSub, InstrumenterNode ctx);

    public void visit(GatewaySubscription sub, ConnectorNode ctx);
}
