package io.gridgo.core.support.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Objects;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BValue;
import io.gridgo.connector.support.config.ConnectorContextBuilder;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.Processor;
import io.gridgo.core.impl.DefaultGridgoContextBuilder;
import io.gridgo.core.support.ContextAwareComponent;
import io.gridgo.core.support.ContextConfiguratorParser;
import io.gridgo.core.support.exceptions.AmbiguousException;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.core.support.subscription.ProcessorSubscription;
import io.gridgo.framework.support.Registry;
import io.gridgo.framework.support.exceptions.BeanNotFoundException;

public class DefaultContextConfigurationParser implements ContextConfiguratorParser {

    @Override
    public GridgoContext parse(Registry registry, BObject configObject) {
        var applicationName = configObject.getString("applicationName", null);
        var gc = new DefaultGridgoContextBuilder().setName(applicationName) //
                                                  .setRegistry(registry) //
                                                  .build();
        parseGateways(registry, configObject, gc);
        parseComponents(registry, configObject, gc);
        return gc;
    }

    private void parseComponents(Registry registry, BObject configObject, GridgoContext gc) {
        configObject.getArray("components") //
                    .stream() //
                    .map(BElement::asValue) //
                    .map(BValue::getString) //
                    .map(e -> this.<ContextAwareComponent>resolve(e, registry)) //
                    .filter(Objects::nonNull) //
                    .forEach(gc::attachComponent);
    }

    private void parseGateways(Registry registry, BObject configObject, GridgoContext gc) {
        configObject.getObject("gateways") //
                    .entrySet().stream() //
                    .sorted(this::compare) //
                    .forEach(e -> openGateway(gc, e, registry));
    }

    @SuppressWarnings("unchecked")
    private <T> T resolve(String bean, Registry registry) {
        var frags = bean.split(":");
        if ("bean".equals(frags[0]))
            return (T) resolveBean(frags[1], registry);
        if ("class".equals(frags[0]))
            return (T) resolveClass(frags[1], registry);
        return (T) resolveRaw(frags[1], registry);
    }

    private String resolveRaw(String name, Registry registry) {
        return registry.substituteRegistriesRecursive(name);
    }

    private Object resolveClass(String name, Registry registry) {
        try {
            var clazz = Class.forName(name);
            var constructors = clazz.getConstructors();
            if (constructors.length > 1)
                throw new AmbiguousException("Only one constructor is allowed");
            var constructor = constructors[0];
            var params = Arrays.stream(constructor.getParameterTypes()) //
                               .map(type -> lookupForType(registry, type)) //
                               .toArray(size -> new Object[size]);
            return constructor.newInstance(params);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Object lookupForType(Registry registry, Class<?> type) {
        if (type == Registry.class)
            return registry;
        var answer = registry.lookupByType(type);
        if (answer == null)
            throw new BeanNotFoundException("Cannot find any bean with the required type " + type.getName());
        return answer;
    }

    private Object resolveBean(String name, Registry registry) {
        return registry.lookupMandatory(name);
    }

    private void openGateway(GridgoContext gc, Entry<String, BElement> e, Registry registry) {
        var gateway = gc.openGateway(e.getKey());
        var val = e.getValue().asObject();

        gateway.setAutoStart(val.getBoolean("autoStart", true));

        var subscribers = val.getArray("subscribers", BArray.ofEmpty());
        for (var subscriber : subscribers) {
            subscribe(gateway, subscriber, registry);
        }

        var connectors = val.getArray("connectors", BArray.ofEmpty());
        for (var connector : connectors) {
            attachConnector(gateway, connector, registry);
        }
    }

    private void attachConnector(GatewaySubscription gateway, BElement connector, Registry registry) {
        if (connector.isValue()) {
            gateway.attachConnector(resolveRaw(connector.asValue().getString(), registry));
            return;
        }
        var obj = connector.asObject();
        var endpoint = resolveRaw(obj.getString("endpoint"), registry);
        var builder = obj.getString("contextBuilder", null);
        if (builder == null) {
            gateway.attachConnector(endpoint);
            return;
        }
        var context = this.<ConnectorContextBuilder>resolve(builder, registry).build();
        gateway.attachConnector(endpoint, context);
    }

    private void subscribe(GatewaySubscription gateway, BElement subscriber, Registry registry) {
        if (subscriber.isValue()) {
            gateway.subscribe(resolve(subscriber.asValue().getString(), registry));
            return;
        }
        var obj = subscriber.asObject();
        var processor = this.<Processor>resolve(obj.getString("processor"), registry);
        var subscription = gateway.subscribe(processor);

        var strategy = obj.getString("executionStrategy", null);
        if (strategy != null)
            subscription.using(resolve(strategy, registry));

        var condition = obj.getString("condition", null);
        if (condition != null)
            subscription.when(condition);

        var instrumenters = obj.getArray("instrumenters", BArray.ofEmpty());
        for (var instrumenter : instrumenters) {
            instrument(subscription, instrumenter, registry);
        }
    }

    private void instrument(ProcessorSubscription subscription, BElement instrumenter, Registry registry) {
        if (instrumenter.isValue()) {
            subscription.instrumentWith(resolve(instrumenter.asValue().getString(), registry));
            return;
        }
    }

    private int compare(Entry<String, BElement> e1, Entry<String, BElement> e2) {
        var order1 = e1.getValue().asObject().getInteger("order", Integer.MAX_VALUE);
        var order2 = e2.getValue().asObject().getInteger("order", Integer.MAX_VALUE);
        return order1 - order2;
    }
}
