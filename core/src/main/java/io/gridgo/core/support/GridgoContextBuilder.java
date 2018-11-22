package io.gridgo.core.support;

import java.util.function.Consumer;

import io.gridgo.connector.ConnectorFactory;
import io.gridgo.core.GridgoContext;
import io.gridgo.framework.support.Builder;
import io.gridgo.framework.support.Registry;
import io.gridgo.framework.support.generators.IdGenerator;

public interface GridgoContextBuilder extends Builder<GridgoContext> {

	public GridgoContextBuilder setConnectorFactory(ConnectorFactory connectorFactory);
	
	public GridgoContextBuilder setRegistry(Registry registry);
	
	public GridgoContextBuilder setExceptionHandler(Consumer<Throwable> exceptionHandler);

	public GridgoContextBuilder setName(String name);

	public GridgoContextBuilder setIdGenerator(IdGenerator idGenerator);
}
