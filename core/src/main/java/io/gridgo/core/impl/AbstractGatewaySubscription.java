package io.gridgo.core.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.joo.libra.PredicateContext;
import org.joo.promise4j.Deferred;

import io.gridgo.connector.Connector;
import io.gridgo.connector.ConnectorResolver;
import io.gridgo.connector.Consumer;
import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.core.Gateway;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.Processor;
import io.gridgo.core.RoutingPolicyEnforcer;
import io.gridgo.core.support.ContextAwareComponent;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.core.support.impl.DefaultRoutingContext;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.core.support.subscription.ProcessorSubscription;
import io.gridgo.core.support.subscription.RoutingPolicy;
import io.gridgo.core.support.subscription.impl.DefaultProcessorSubscription;
import io.gridgo.framework.AbstractComponentLifecycle;
import io.gridgo.framework.support.Message;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import lombok.Getter;

@Getter
public abstract class AbstractGatewaySubscription extends AbstractComponentLifecycle
        implements Gateway, GatewaySubscription {

    private String name;

    private GridgoContext context;

    private List<Connector> connectors = new CopyOnWriteArrayList<>();

    private List<ProcessorSubscription> subscriptions = new CopyOnWriteArrayList<>();

    private RoutingPolicyEnforcer[] policyEnforcers = new RoutingPolicyEnforcer[0];

    private Subject<RoutingContext> subject = PublishSubject.create();

    public AbstractGatewaySubscription(GridgoContext context, String name) {
        this.context = context;
        this.name = name;
        this.subject.subscribe(this::handleMessages);
    }

    @Override
    public GatewaySubscription attachConnector(String endpoint) {
        var connector = context.getConnectorFactory().createConnector(endpoint);
        return attachConnector(connector);
    }

    @Override
    public GatewaySubscription attachConnector(String endpoint, ConnectorResolver resolver) {
        var connector = context.getConnectorFactory().createConnector(endpoint, resolver);
        return attachConnector(connector);
    }

    @Override
    public GatewaySubscription attachConnector(String endpoint, ConnectorContext connectorContext) {
        var connector = context.getConnectorFactory().createConnector(endpoint, connectorContext);
        return attachConnector(connector);
    }

    @Override
    public GatewaySubscription attachConnector(String endpoint, ConnectorResolver resolver,
            ConnectorContext connectorContext) {
        var connector = context.getConnectorFactory().createConnector(endpoint, resolver, connectorContext);
        return attachConnector(connector);
    }

    @Override
    public GatewaySubscription attachConnector(Connector connector) {
        connectors.add(connector);
        subscribeConnector(connector);
        return this;
    }

    private void subscribeConnector(Connector connector) {
        connector.getConsumer().ifPresent(this::subscribeConsumer);
    }

    private void subscribeConsumer(Consumer consumer) {
        consumer.subscribe(this::publish);
    }

    private void handleMessages(RoutingContext rc) {
        var predicateContext = new PredicateContext(rc.getMessage());
        for (var enforcer : policyEnforcers) {
            enforcer.execute(rc, context, predicateContext);
        }
    }

    protected void publish(Message msg, Deferred<Message, Exception> deferred) {
        var routingContext = new DefaultRoutingContext(this, msg, deferred);
        subject.onNext(routingContext);
    }

    @Override
    public GatewaySubscription attachRoutingPolicy(RoutingPolicy policy) {
        var subscription = new DefaultProcessorSubscription(this, policy);
        subscriptions.add(subscription);
        return this;
    }

    @Override
    public ProcessorSubscription subscribe(Processor processor) {
        if (processor instanceof ContextAwareComponent)
            ((ContextAwareComponent) processor).setContext(context);
        var subscription = new DefaultProcessorSubscription(this, processor);
        subscriptions.add(subscription);
        return subscription;
    }

    @Override
    public Observable<RoutingContext> asObservable() {
        return subject.publish().autoConnect();
    }

    @Override
    public Gateway get() {
        return this;
    }

    @Override
    protected void onStart() {
        this.policyEnforcers = subscriptions.stream() //
                                            .map(ProcessorSubscription::getPolicy) //
                                            .map(DefaultRoutingPolicyEnforcer::new)
                                            .toArray(size -> new RoutingPolicyEnforcer[size]);

        for (Connector connector : connectors)
            connector.start();
    }

    @Override
    protected void onStop() {
        for (Connector connector : connectors)
            connector.stop();
    }

    @Override
    protected String generateName() {
        return "gateway." + name;
    }
}
