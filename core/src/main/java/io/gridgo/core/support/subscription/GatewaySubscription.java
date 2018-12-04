package io.gridgo.core.support.subscription;

import io.gridgo.connector.Connector;
import io.gridgo.connector.ConnectorResolver;
import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.core.Gateway;
import io.gridgo.core.Processor;

public interface GatewaySubscription {

	public GatewaySubscription attachConnector(String endpoint);

	public GatewaySubscription attachConnector(String endpoint, ConnectorResolver resolver);

	public GatewaySubscription attachConnector(String endpoint, ConnectorContext connectorContext);

	public GatewaySubscription attachConnector(String endpoint, ConnectorResolver resolver,
			ConnectorContext connectorContext);

	public GatewaySubscription attachConnector(Connector connector);

	public GatewaySubscription attachRoutingPolicy(RoutingPolicy policy);

	public HandlerSubscription subscribe(Processor processor);

	public GatewaySubscription setAutoStart(boolean autoStart);

	public boolean isAutoStart();

	public Gateway get();
}
