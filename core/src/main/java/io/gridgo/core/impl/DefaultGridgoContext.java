package io.gridgo.core.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import io.gridgo.connector.ConnectorFactory;
import io.gridgo.connector.impl.factories.DefaultConnectorFactory;
import io.gridgo.core.Gateway;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.ContextAwareComponent;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.core.support.template.ProducerTemplate;
import io.gridgo.framework.AbstractComponentLifecycle;
import io.gridgo.framework.ComponentLifecycle;
import io.gridgo.framework.support.Registry;
import io.gridgo.framework.support.impl.SimpleRegistry;
import lombok.Getter;

public class DefaultGridgoContext extends AbstractComponentLifecycle implements GridgoContext {

    private static final Consumer<Throwable> DEFAULT_EXCEPTION_HANDLER = ex -> {
    };

    private Map<String, GatewaySubscription> gateways = new ConcurrentHashMap<>();

    private Map<String, Long> gatewayOrder = new ConcurrentHashMap<>();

    private AtomicLong counter = new AtomicLong(0);

    private String name;

    @Getter
    private ConnectorFactory connectorFactory = new DefaultConnectorFactory();

    @Getter
    private Registry registry = new SimpleRegistry();

    @Getter
    private Consumer<Throwable> exceptionHandler = DEFAULT_EXCEPTION_HANDLER;

    @Getter
    private List<ComponentLifecycle> components = new CopyOnWriteArrayList<>();

    protected DefaultGridgoContext(String name, ConnectorFactory connectorFactory, Registry registry,
            Consumer<Throwable> exceptionHandler) {
        this.name = name != null ? name : UUID.randomUUID().toString();
        if (connectorFactory != null)
            this.connectorFactory = connectorFactory;
        if (registry != null) {
            this.registry = registry;
            this.connectorFactory.setRegistry(registry);
        }
        if (exceptionHandler != null)
            this.exceptionHandler = exceptionHandler;
    }

    @Override
    public GatewaySubscription openGateway(String name, ProducerTemplate producerTemplate) {
        return gateways.computeIfAbsent(name, key -> {
            gatewayOrder.put(name, counter.incrementAndGet());
            return new DefaultGateway(this, key).setProducerTemplate(producerTemplate);
        });
    }

    @Override
    public Optional<Gateway> closeGateway(String name) {
        var gateway = gateways.remove(name);
        if (gateway == null)
            return Optional.empty();
        return Optional.of(gateway.get());
    }

    @Override
    public Optional<Gateway> findGateway(String name) {
        var gateway = gateways.get(name);
        if (gateway == null)
            return Optional.empty();
        return Optional.of(gateway.get());
    }

    @Override
    protected void onStart() {
        components.stream().forEach(ComponentLifecycle::start);
        gateways.entrySet().stream() //
                .sorted(this::compareGateways) //
                .map(entry -> entry.getValue().get()) //
                .filter(Gateway::isAutoStart) //
                .forEach(Gateway::start);
    }

    private int compareGateways(Entry<String, GatewaySubscription> e1, Entry<String, GatewaySubscription> e2) {
        return gatewayOrder.get(e1.getKey()) > gatewayOrder.get(e2.getKey()) ? 1 : -1;
    }

    @Override
    protected void onStop() {
        gateways.values().stream().forEach(g -> g.get().stop());
        components.stream().forEach(ComponentLifecycle::stop);
    }

    @Override
    public Collection<GatewaySubscription> getGateways() {
        return Collections.unmodifiableCollection(gateways.values());
    }

    @Override
    public Map<String, GatewaySubscription> getGatewaysWithNames() {
        return Collections.unmodifiableMap(gateways);
    }

    @Override
    public Optional<GatewaySubscription> getGatewaySubscription(String name) {
        return Optional.ofNullable(gateways.get(name));
    }

    @Override
    public GridgoContext attachComponent(ContextAwareComponent component) {
        components.add(component);
        component.setContext(this);
        return this;
    }

    @Override
    protected String generateName() {
        return "context." + name;
    }
}
