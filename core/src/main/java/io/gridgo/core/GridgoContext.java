package io.gridgo.core;

import java.util.Optional;
import java.util.function.Consumer;

import io.gridgo.connector.ConnectorFactory;
import io.gridgo.core.support.ContextAwareComponent;
import io.gridgo.core.support.GatewayContainer;
import io.gridgo.core.support.ProducerJoinMode;
import io.gridgo.core.support.exceptions.InvalidGatewayException;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.core.support.template.ProducerTemplate;
import io.gridgo.framework.ComponentLifecycle;
import io.gridgo.framework.support.Registry;

public interface GridgoContext extends GatewayContainer, ComponentLifecycle {

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
}
