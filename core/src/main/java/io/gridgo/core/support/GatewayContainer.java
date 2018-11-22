package io.gridgo.core.support;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import io.gridgo.core.Gateway;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.core.support.template.ProducerTemplate;

public interface GatewayContainer {

	public GatewaySubscription openGateway(String name);

	public GatewaySubscription openGateway(String name, ProducerTemplate producerTemplate);

	public GatewaySubscription openGateway(String name, ProducerJoinMode joinMode);

	public Optional<Gateway> closeGateway(String name);

	public Optional<Gateway> findGateway(String name);

	public Collection<Gateway> getGateways();

	public Map<String, Gateway> getGatewaysWithNames();
}
