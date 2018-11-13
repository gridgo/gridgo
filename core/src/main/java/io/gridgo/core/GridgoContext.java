package io.gridgo.core;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import io.gridgo.connector.ConnectorFactory;
import io.gridgo.core.support.ProducerJoinMode;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.core.support.template.ProducerTemplate;
import io.gridgo.framework.ComponentLifecycle;
import io.gridgo.framework.support.Registry;

public interface GridgoContext extends ComponentLifecycle {

	public GatewaySubscription openGateway(String name);

	public GatewaySubscription openGateway(String name, ProducerTemplate producerTemplate);

	public GatewaySubscription openGateway(String name, ProducerJoinMode joinMode);

	public Optional<Gateway> closeGateway(String name);

	public Optional<Gateway> findGateway(String name);

	public Collection<Gateway> getGateways();

	public Map<String, Gateway> getGatewaysWithNames();

	public Registry getRegistry();

	public ConnectorFactory getConnectorFactory();

	public Consumer<Throwable> getExceptionHandler();
}
