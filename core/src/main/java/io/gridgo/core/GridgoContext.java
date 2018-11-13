package io.gridgo.core;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.gridgo.framework.ComponentLifecycle;

public interface GridgoContext extends ComponentLifecycle {

	public Gateway openGateway(String name);

	public Gateway closeGateway(String name);

	public Optional<Gateway> findGateway(String name);

	public List<Gateway> getGateways();

	public Map<String, Gateway> getGatewaysWithNames();
}
