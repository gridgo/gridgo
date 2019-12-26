package io.gridgo.core;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import io.gridgo.connector.ConnectorFactory;
import io.gridgo.core.support.ContextAwareComponent;
import io.gridgo.core.support.GatewayContainer;
import io.gridgo.core.support.exceptions.InvalidGatewayException;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.framework.ComponentLifecycle;
import io.gridgo.framework.support.Registry;

/**
 * Represents a context in which a Gridgo application runs.
 */
public interface GridgoContext extends GatewayContainer, ComponentLifecycle {

    /**
     * Attach a <link>io.gridgo.core.support.ContextAwareComponent</link> to this
     * context.
     *
     * @param component the component to be attached
     * @return this context
     */
    public GridgoContext attachComponent(ContextAwareComponent component);

    public default Gateway findGatewayMandatory(String name) {
        return findGateway(name).orElseThrow(() -> new InvalidGatewayException(name));
    }

    public default GridgoContext startGateway(String name) {
        findGatewayMandatory(name).start();
        return this;
    }

    public default GridgoContext stopGateway(String name) {
        findGatewayMandatory(name).stop();
        return this;
    }

    public Optional<GatewaySubscription> getGatewaySubscription(String name);

    public Registry getRegistry();

    public ConnectorFactory getConnectorFactory();

    public Consumer<Throwable> getExceptionHandler();

    public List<ContextAwareComponent> getComponents();
}
