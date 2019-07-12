package io.gridgo.core.support.impl;

import java.util.Optional;
import java.util.stream.Collectors;

import io.gridgo.bean.BObject;
import io.gridgo.connector.Connector;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.Processor;
import io.gridgo.core.support.ContextAwareComponent;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.core.support.subscription.ProcessorSubscription;
import io.gridgo.framework.ComponentLifecycle;
import io.gridgo.framework.support.Message;

public class ContextSpoofingProcessor implements Processor {

    @Override
    public void process(RoutingContext rc, GridgoContext gc) {
        rc.getDeferred().resolve(spoofContext(gc));
    }

    protected Message spoofContext(GridgoContext gc) {
        var gateways = BObject.ofEmpty();
        for (var entry : gc.getGatewaysWithNames().entrySet()) {
            gateways.setAny(entry.getKey(), spoofGateway(entry.getValue()));
        }
        var components = gc.getComponents().stream() //
                           .map(this::spoofComponent) //
                           .collect(Collectors.toList());
        var result = BObject.of("name", gc.getName()) //
                            .setAny("gateways", gateways) //
                            .setAny("components", components);
        var msg = Message.ofAny(result);
        return msg;
    }

    protected BObject spoofComponent(ContextAwareComponent component) {
        return BObject.of("name", component.getName()) //
                      .setAny("class", component.getClass().getName()) //
                      .setAny("started", component.isStarted());
    }

    protected BObject spoofGateway(GatewaySubscription subscription) {
        var connectors = subscription.get() //
                                     .getConnectors().stream() //
                                     .map(this::spoofConnector) //
                                     .collect(Collectors.toList());
        var processors = subscription.getSubscriptions().stream() //
                                     .map(this::spoofSubscription) //
                                     .collect(Collectors.toList());
        return BObject.of("connectors", connectors) //
                      .setAny("subscriptions", processors) //
                      .setAny("started", subscription.get().isStarted()) //
                      .setAny("autoStart", subscription.get().isAutoStart());
    }

    protected BObject spoofConnector(Connector connector) {
        return BObject.of("endpoint", getFullEndpoint(connector)) //
                      .setAny("scheme", connector.getConnectorConfig().getScheme()) //
                      .setAny("started", connector.isStarted()) //
                      .setAny("consumer", extractName(connector.getConsumer())) //
                      .setAny("producer", extractName(connector.getProducer()));
    }

    private String getFullEndpoint(Connector connector) {
        var config = connector.getConnectorConfig();
        return config.getScheme() + ":" + config.getOriginalEndpoint();
    }

    protected BObject spoofSubscription(ProcessorSubscription sub) {
        var policy = sub.getPolicy();
        return BObject.of("processor", spoofProcessor(policy.getProcessor())) //
                      .setAny("strategy", extractName(policy.getStrategy())) //
                      .setAny("instrumenter", policy.getInstrumenter().map(i -> i.getClass().getName()).orElse(null));
    }

    protected BObject spoofProcessor(Processor processor) {
        return BObject.of("class", processor.getClass().getName());
    }

    private String extractName(Optional<? extends ComponentLifecycle> component) {
        return component.map(c -> c.getName()).orElse(null);
    }
}
