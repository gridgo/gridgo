package io.gridgo.core;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import io.gridgo.connector.ConnectorFactory;
import io.gridgo.core.support.ContextAwareComponent;
import io.gridgo.core.support.ProducerJoinMode;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.core.support.template.ProducerTemplate;
import io.gridgo.framework.ComponentLifecycle;
import io.gridgo.framework.support.Registry;

public interface GridgoContext extends ComponentLifecycle {

	public GridgoContext attachComponent(ContextAwareComponent component);

	public default GatewaySubscription openGateway(String name) {
		return openGateway(name, ProducerJoinMode.SINGLE);
	}

	public default GatewaySubscription openGateway(String name, ProducerJoinMode joinMode) {
		return openGateway(name, ProducerTemplate.create(joinMode));
	}

	public GatewaySubscription openGateway(String name, ProducerTemplate producerTemplate);

	public Optional<Gateway> closeGateway(String name);

	public Optional<Gateway> findGateway(String name);

	public Collection<Gateway> getGateways();

	public Map<String, Gateway> getGatewaysWithNames();

	public Registry getRegistry();

	public ConnectorFactory getConnectorFactory();

	public Consumer<Throwable> getExceptionHandler();
}
