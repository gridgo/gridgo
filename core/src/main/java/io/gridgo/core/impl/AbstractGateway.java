package io.gridgo.core.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.gridgo.connector.Connector;
import io.gridgo.connector.ConnectorResolver;
import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.core.Gateway;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.Processor;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.core.support.subscription.HandlerSubscription;
import io.gridgo.core.support.subscription.RoutingPolicy;
import io.gridgo.core.support.subscription.impl.DefaultHandlerSubscription;
import io.gridgo.framework.AbstractComponentLifecycle;
import lombok.Getter;

@Getter
public abstract class AbstractGateway extends AbstractComponentLifecycle implements Gateway {

	private String name;

	private GridgoContext context;

	private List<Connector> connectors = new CopyOnWriteArrayList<>();

	private List<HandlerSubscription> subscriptions = new CopyOnWriteArrayList<>();

	public AbstractGateway(GridgoContext context, String name) {
		this.context = context;
		this.name = name;
	}

	@Override
	public GatewaySubscription attachConnector(String endpoint) {
		var connector = context.getConnectorFactory().createConnector(endpoint);
		connectors.add(connector);
		return this;
	}

	@Override
	public GatewaySubscription attachConnector(String endpoint, ConnectorResolver resolver) {
		var connector = context.getConnectorFactory().createConnector(endpoint, resolver);
		connectors.add(connector);
		return this;
	}

	@Override
	public GatewaySubscription attachConnector(String endpoint, ConnectorContext connectorContext) {
		var connector = context.getConnectorFactory().createConnector(endpoint, connectorContext);
		connectors.add(connector);
		return this;
	}

	@Override
	public GatewaySubscription attachConnector(String endpoint, ConnectorResolver resolver,
			ConnectorContext connectorContext) {
		var connector = context.getConnectorFactory().createConnector(endpoint, resolver, connectorContext);
		connectors.add(connector);
		return this;
	}

	@Override
	public GatewaySubscription attachConnector(Connector connector) {
		connectors.add(connector);
		return this;
	}

	@Override
	public GatewaySubscription attachRoutingPolicy(RoutingPolicy policy) {
		var subscription = new DefaultHandlerSubscription(this, policy);
		subscriptions.add(subscription);
		return this;
	}

	@Override
	public HandlerSubscription subscribe(Processor processor) {
		var subscription = new DefaultHandlerSubscription(this, processor);
		subscriptions.add(subscription);
		return subscription;
	}

	@Override
	public Gateway get() {
		return this;
	}

	@Override
	protected void onStart() {
		for (Connector connector : connectors)
			connector.start();
	}

	@Override
	protected void onStop() {
		for (Connector connector : connectors)
			connector.stop();
	}

	@Override
	protected String generateName() {
		return "gateway." + name;
	}
}
