package io.gridgo.core.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import io.gridgo.connector.ConnectorFactory;
import io.gridgo.connector.impl.factories.DefaultConnectorFactory;
import io.gridgo.core.Gateway;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.ProducerJoinMode;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.core.support.template.ProducerTemplate;
import io.gridgo.framework.AbstractComponentLifecycle;
import io.gridgo.framework.support.Registry;
import io.gridgo.framework.support.impl.SimpleRegistry;
import lombok.Getter;

public class DefaultGridgoContext extends AbstractComponentLifecycle implements GridgoContext {

	private static final Consumer<Throwable> DEFAULT_EXCEPTION_HANDLER = ex -> {
	};

	private Map<String, Gateway> gateways = new ConcurrentHashMap<>();

	private String name;

	@Getter
	private ConnectorFactory connectorFactory = new DefaultConnectorFactory();

	@Getter
	private Registry registry = new SimpleRegistry();

	@Getter
	private Consumer<Throwable> exceptionHandler = DEFAULT_EXCEPTION_HANDLER;

	protected DefaultGridgoContext(String name, ConnectorFactory connectorFactory, Registry registry,
			Consumer<Throwable> exceptionHandler) {
		this.name = name != null ? name : UUID.randomUUID().toString();
		if (connectorFactory != null)
			this.connectorFactory = connectorFactory;
		if (registry != null)
			this.registry = registry;
		if (exceptionHandler != null)
			this.exceptionHandler = exceptionHandler;
		this.connectorFactory.setRegistry(registry);
	}

	@Override
	public GatewaySubscription openGateway(String name) {
		return gateways.computeIfAbsent(name, key -> new DefaultGateway(this, key));
	}

	@Override
	public GatewaySubscription openGateway(String name, ProducerJoinMode joinMode) {
		return openGateway(name, ProducerTemplate.create(joinMode));
	}

	@Override
	public GatewaySubscription openGateway(String name, ProducerTemplate producerTemplate) {
		return gateways.computeIfAbsent(name,
				key -> new DefaultGateway(this, key).setProducerTemplate(producerTemplate));
	}

	@Override
	public Optional<Gateway> closeGateway(String name) {
		var gateway = gateways.remove(name);
		return Optional.ofNullable(gateway);
	}

	@Override
	public Optional<Gateway> findGateway(String name) {
		return Optional.ofNullable(gateways.get(name));
	}

	@Override
	public Collection<Gateway> getGateways() {
		return Collections.unmodifiableCollection(gateways.values());
	}

	@Override
	public Map<String, Gateway> getGatewaysWithNames() {
		return Collections.unmodifiableMap(gateways);
	}

	@Override
	protected void onStart() {
		gateways.values().stream().forEach(g -> g.start());
	}

	@Override
	protected void onStop() {
		getGateways().stream().forEach(g -> g.stop());
	}

	@Override
	protected String generateName() {
		return "context." + name;
	}
}
