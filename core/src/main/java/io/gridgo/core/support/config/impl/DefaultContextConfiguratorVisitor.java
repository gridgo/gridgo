package io.gridgo.core.support.config.impl;

import io.gridgo.connector.support.config.ConnectorContextBuilder;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.impl.DefaultGridgoContextBuilder;
import io.gridgo.core.support.config.ConfiguratorNode.ComponentNode;
import io.gridgo.core.support.config.ConfiguratorNode.ConnectorNode;
import io.gridgo.core.support.config.ConfiguratorNode.GatewayNode;
import io.gridgo.core.support.config.ConfiguratorNode.InstrumenterNode;
import io.gridgo.core.support.config.ConfiguratorNode.RootNode;
import io.gridgo.core.support.config.ConfiguratorNode.SubscriberNode;
import io.gridgo.core.support.config.ConfiguratorResolver;
import io.gridgo.core.support.config.ContextConfiguratorVisitor;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.core.support.subscription.ProcessorSubscription;
import io.gridgo.framework.support.Registry;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefaultContextConfiguratorVisitor implements ContextConfiguratorVisitor, ConfiguratorResolver {

    private Registry registry;

    @Override
    public GridgoContext visit(RootNode rootContext) {
        var gc = new DefaultGridgoContextBuilder().setName(rootContext.getApplicationName()) //
                                                  .setConnectorFactory(resolve(rootContext.getConnectorFactory())) //
                                                  .setRegistry(registry) //
                                                  .build();
        rootContext.visitChildren(this, gc);
        return gc;
    }

    @Override
    public void visit(GridgoContext gc, ComponentNode ctx) {
        gc.attachComponent(resolve(ctx.getComponent()));
    }

    @Override
    public void visit(GridgoContext gc, GatewayNode ctx) {
        var sub = gc.openGateway(ctx.getName()) //
                    .setAutoStart(ctx.isAutoStart());
        ctx.visitChildren(this, sub);
    }

    @Override
    public void visit(GatewaySubscription sub, SubscriberNode ctx) {
        var procSub = sub.subscribe(resolve(ctx.getProcessor()));
        if (ctx.getCondition() != null)
            procSub.when(ctx.getCondition());
        if (ctx.getExecutionStrategy() != null)
            procSub.using(resolve(ctx.getExecutionStrategy()));
        ctx.visitChildren(this, procSub);
    }

    @Override
    public void visit(ProcessorSubscription procSub, InstrumenterNode ctx) {
        if (ctx.getCondition() != null)
            procSub.instrumentWhen(ctx.getCondition(), resolve(ctx.getInstrumenter()));
        else
            procSub.instrumentWith(resolve(ctx.getInstrumenter()));
    }

    @Override
    public void visit(GatewaySubscription sub, ConnectorNode ctx) {
        var endpoint = registry.substituteRegistriesRecursive(ctx.getEndpoint());
        if (ctx.getContextBuilder() != null) {
            ConnectorContextBuilder builder = resolve(ctx.getContextBuilder());
            sub.attachConnector(endpoint, builder.build());
        } else {
            sub.attachConnector(endpoint);
        }
    }
}
