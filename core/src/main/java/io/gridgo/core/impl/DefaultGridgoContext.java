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
import io.gridgo.core.support.LifecycleEvent;
import io.gridgo.core.support.LifecycleEventPublisher;
import io.gridgo.core.support.LifecycleType;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.core.support.template.ProducerTemplate;
import io.gridgo.framework.impl.SubjectEventDispatcher;
import io.gridgo.framework.support.Registry;
import io.gridgo.framework.support.impl.SimpleRegistry;
import io.reactivex.subjects.ReplaySubject;
import lombok.Getter;

public class DefaultGridgoContext extends SubjectEventDispatcher<LifecycleEvent> implements GridgoContext, LifecycleEventPublisher {

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
    private List<ContextAwareComponent> components = new CopyOnWriteArrayList<>();

    protected DefaultGridgoContext(String name, ConnectorFactory connectorFactory, Registry registry,
            Consumer<Throwable> exceptionHandler) {
        super(ReplaySubject.create());
        this.name = name != null ? name : UUID.randomUUID().toString();
        if (connectorFactory != null) {
            this.connectorFactory = connectorFactory;
        }
        if (registry != null) {
            this.registry = registry;
            this.connectorFactory.setRegistry(registry);
        }
        if (exceptionHandler != null) {
            this.exceptionHandler = exceptionHandler;
        }
    }

    @Override
    public GatewaySubscription openGateway(String name, ProducerTemplate producerTemplate) {
        var sub = gateways.computeIfAbsent(name, key -> {
            gatewayOrder.put(name, counter.incrementAndGet());
            return new DefaultGateway(this, key).setProducerTemplate(producerTemplate);
        });
        publish(LifecycleType.GATEWAY_OPENED, sub.get());
        return sub;
    }

    @Override
    public Optional<Gateway> closeGateway(String name) {
        var gateway = gateways.remove(name);
        if (gateway == null)
            return Optional.empty();
        publish(LifecycleType.GATEWAY_CLOSED, gateway.get());
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
        components.stream().forEach(this::startComponent);
        gateways.entrySet().stream() //
                .sorted(this::compareGateways) //
                .map(entry -> entry.getValue().get()) //
                .filter(Gateway::isAutoStart) //
                .forEach(Gateway::start);
        publish(LifecycleType.CONTEXT_STARTED, this);
    }

    private int compareGateways(Entry<String, GatewaySubscription> e1, Entry<String, GatewaySubscription> e2) {
        return gatewayOrder.get(e1.getKey()) > gatewayOrder.get(e2.getKey()) ? 1 : -1;
    }

    @Override
    protected void onStop() {
        gateways.values().stream().forEach(g -> g.get().stop());
        components.stream().forEach(this::stopComponent);
        publish(LifecycleType.CONTEXT_STOPPED, this);
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
        publish(LifecycleType.COMPONENT_ATTACHED, component);
        return this;
    }

    private void startComponent(ContextAwareComponent component) {
        component.start();
        publish(LifecycleType.COMPONENT_STARTED, component);
    }

    private void stopComponent(ContextAwareComponent component) {
        component.stop();
        publish(LifecycleType.COMPONENT_STOPPED, component);
    }

    @Override
    protected String generateName() {
        return "context." + name;
    }

    @Override
    public GridgoContext getContext() {
        return this;
    }
}
