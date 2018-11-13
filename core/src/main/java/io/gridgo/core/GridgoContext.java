package io.gridgo.core;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.gridgo.connector.ConnectorFactory;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.framework.ComponentLifecycle;

public interface GridgoContext extends ComponentLifecycle {

	public GatewaySubscription openGateway(String name);

	public Optional<Gateway> closeGateway(String name);

	public Optional<Gateway> findGateway(String name);

	public List<Gateway> getGateways();

	public Map<String, Gateway> getGatewaysWithNames();

	public ConnectorFactory getConnectorFactory();
}
