package io.gridgo.core.support.subscription.impl;

import io.gridgo.connector.Connector;
import io.gridgo.connector.ConnectorResolver;
import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.core.Gateway;
import io.gridgo.core.Processor;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.core.support.subscription.HandlerSubscription;
import io.gridgo.core.support.subscription.RoutingPolicy;

public class DefaultGatewaySubscription implements GatewaySubscription {

	private Gateway gateway;

	public DefaultGatewaySubscription(Gateway gateway) {
		this.gateway = gateway;
	}

	@Override
	public GatewaySubscription attachConnector(String endpoint) {
		return this.gateway.attachConnector(endpoint);
	}

	@Override
	public GatewaySubscription attachConnector(String endpoint, ConnectorResolver resolver) {
		return this.gateway.attachConnector(endpoint, resolver);
	}

	@Override
	public GatewaySubscription attachConnector(String endpoint, ConnectorContext connectorContext) {
		return this.gateway.attachConnector(endpoint, connectorContext);
	}

	@Override
	public GatewaySubscription attachConnector(String endpoint, ConnectorResolver resolver,
			ConnectorContext connectorContext) {
		return this.gateway.attachConnector(endpoint, resolver, connectorContext);
	}

	@Override
	public GatewaySubscription attachConnector(Connector connector) {
		return this.gateway.attachConnector(connector);
	}

	@Override
	public GatewaySubscription attachRoutingPolicy(RoutingPolicy policy) {
		return this.gateway.attachRoutingPolicy(policy);
	}

	@Override
	public HandlerSubscription subscribe(Processor processor) {
		return this.gateway.subscribe(processor);
	}

	@Override
	public Gateway get() {
		return this.gateway;
	}
}
