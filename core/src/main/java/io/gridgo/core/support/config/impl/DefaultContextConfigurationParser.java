package io.gridgo.core.support.config.impl;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.core.support.config.ConfiguratorNode.ComponentNode;
import io.gridgo.core.support.config.ConfiguratorNode.ConnectorNode;
import io.gridgo.core.support.config.ConfiguratorNode.GatewayNode;
import io.gridgo.core.support.config.ConfiguratorNode.InstrumenterNode;
import io.gridgo.core.support.config.ConfiguratorNode.RootNode;
import io.gridgo.core.support.config.ConfiguratorNode.SubscriberNode;
import io.gridgo.core.support.config.ContextConfiguratorParser;

public class DefaultContextConfigurationParser implements ContextConfiguratorParser {

    @Override
    public RootNode parse(BObject configObject) {
        var applicationName = configObject.getString("applicationName", null);
        var connectorFactory = configObject.getString("connectorFactory", null);
        var gateways = parseGateways(configObject.getObject("gateways", BObject.ofEmpty()));
        var components = parseComponents(configObject.getArray("components", BArray.ofEmpty()));
        return RootNode.builder() //
                       .applicationName(applicationName) //
                       .connectorFactory(connectorFactory) //
                       .gatewayContexts(gateways) //
                       .componentsContexts(components) //
                       .build();
    }

    private List<ComponentNode> parseComponents(BArray components) {
        return components.stream() //
                         .map(this::parseComponent) //
                         .collect(Collectors.toList());
    }

    private ComponentNode parseComponent(BElement component) {
        return ComponentNode.builder().component(component.asValue().getString()).build();
    }

    private List<GatewayNode> parseGateways(BObject gateways) {
        return gateways.entrySet().stream() //
                       .sorted(this::compare) //
                       .map(this::parseGateway) //
                       .collect(Collectors.toList());
    }

    private GatewayNode parseGateway(Entry<String, BElement> entry) {
        var value = entry.getValue().asObject();
        var subscribers = parseSubscribers(value.getArray("subscribers", BArray.ofEmpty()));
        var connectors = parseConnectors(value.getArray("connectors", BArray.ofEmpty()));
        return GatewayNode.builder() //
                          .name(entry.getKey()) //
                          .autoStart(value.getBoolean("autoStart", true)) //
                          .subscriberContexts(subscribers) //
                          .connectorContexts(connectors).build();
    }

    private List<ConnectorNode> parseConnectors(BArray connectors) {
        return connectors.stream() //
                         .map(this::parseConnector) //
                         .collect(Collectors.toList());
    }

    private ConnectorNode parseConnector(BElement connector) {
        if (connector.isValue()) {
            var endpoint = connector.asValue().getString();
            return ConnectorNode.builder().endpoint(endpoint).build();
        }
        var obj = connector.asObject();
        var endpoint = obj.getString("endpoint");
        var builder = obj.getString("contextBuilder", null);
        return ConnectorNode.builder() //
                            .endpoint(endpoint) //
                            .contextBuilder(builder) //
                            .build();
    }

    private List<SubscriberNode> parseSubscribers(BArray subscribers) {
        return subscribers.stream() //
                          .map(this::parseSubscriber) //
                          .collect(Collectors.toList());
    }

    private SubscriberNode parseSubscriber(BElement subscriber) {
        if (subscriber.isValue()) {
            return SubscriberNode.builder() //
                                 .processor(subscriber.asValue().getString()) //
                                 .build();
        }
        var obj = subscriber.asObject();
        var processor = obj.getString("processor");
        var strategy = obj.getString("executionStrategy", null);
        var condition = obj.getString("condition", null);
        var instrumenters = parseInstrumenters(obj.getArray("instrumenters", BArray.ofEmpty()));
        return SubscriberNode.builder() //
                             .processor(processor) //
                             .executionStrategy(strategy) //
                             .condition(condition) //
                             .instrumenterContexts(instrumenters) //
                             .build();
    }

    private List<InstrumenterNode> parseInstrumenters(BArray instrumenters) {
        return instrumenters.stream() //
                            .map(this::parseInstrumenter) //
                            .collect(Collectors.toList());
    }

    private InstrumenterNode parseInstrumenter(BElement instrumenter) {
        if (instrumenter.isValue()) {
            var instrument = instrumenter.asValue().getString();
            return InstrumenterNode.builder() //
                                   .instrumenter(instrument) //
                                   .build();
        }
        var obj = instrumenter.asObject();
        var instrument = obj.getString("instrumenter");
        var condition = obj.getString("condition", null);
        return InstrumenterNode.builder() //
                               .instrumenter(instrument) //
                               .condition(condition) //
                               .build();
    }

    private int compare(Entry<String, BElement> e1, Entry<String, BElement> e2) {
        var order1 = e1.getValue().asObject().getInteger("order", Integer.MAX_VALUE);
        var order2 = e2.getValue().asObject().getInteger("order", Integer.MAX_VALUE);
        return order1 - order2;
    }
}
