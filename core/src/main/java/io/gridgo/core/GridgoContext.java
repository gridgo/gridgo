package io.gridgo.core;

import java.util.Optional;
import java.util.function.Consumer;

import io.gridgo.connector.ConnectorFactory;
import io.gridgo.core.support.ContextAwareComponent;
import io.gridgo.core.support.Feature;
import io.gridgo.core.support.GatewayContainer;
import io.gridgo.framework.ComponentLifecycle;
import io.gridgo.framework.support.Registry;
import io.gridgo.framework.support.generators.IdGenerator;

public interface GridgoContext extends GatewayContainer, ComponentLifecycle {

	public GridgoContext attachComponent(ContextAwareComponent component);

	public Registry getRegistry();

	public ConnectorFactory getConnectorFactory();

	public Consumer<Throwable> getExceptionHandler();
	
	public Optional<IdGenerator> getIdGenerator();

	public GridgoContext enableFeature(Feature feature);

	public GridgoContext disableFeature(Feature feature);

	public boolean isFeatureEnabled(Feature feature);
}
