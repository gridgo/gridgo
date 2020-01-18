package io.gridgo.core.impl;

import org.joo.libra.PredicateContext;
import org.joo.promise4j.Deferred;
import org.joo.promise4j.impl.CompletableDeferredObject;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.gridgo.connector.Connector;
import io.gridgo.connector.ConnectorResolver;
import io.gridgo.connector.Consumer;
import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.connector.support.exceptions.NoSubscriberException;
import io.gridgo.core.Gateway;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.Processor;
import io.gridgo.core.RoutingPolicyEnforcer;
import io.gridgo.core.support.ContextAwareComponent;
import io.gridgo.core.support.LifecycleEventPublisher;
import io.gridgo.core.support.LifecycleType;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.core.support.impl.DefaultRoutingContext;
import io.gridgo.core.support.subscription.ConnectorAttachment;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.core.support.subscription.ProcessorSubscription;
import io.gridgo.core.support.subscription.RoutingPolicy;
import io.gridgo.core.support.subscription.impl.DefaultConnectorAttachment;
import io.gridgo.core.support.subscription.impl.DefaultProcessorSubscription;
import io.gridgo.framework.impl.AbstractComponentLifecycle;
import io.gridgo.framework.support.Message;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import lombok.Getter;

@Getter
public abstract class AbstractGatewaySubscription extends AbstractComponentLifecycle
        implements Gateway, GatewaySubscription, LifecycleEventPublisher {

    private String name;

    private GridgoContext context;

    private List<ConnectorAttachment> connectorAttachments = new CopyOnWriteArrayList<>();

    private List<ProcessorSubscription> subscriptions = new CopyOnWriteArrayList<>();

    private RoutingPolicyEnforcer[] policyEnforcers = new RoutingPolicyEnforcer[0];

    private Subject<RoutingContext> subject = PublishSubject.create();

    public AbstractGatewaySubscription(GridgoContext context, String name) {
        this.context = context;
        this.name = name;
    }

    @Override
    public ConnectorAttachment attachConnector(String endpoint) {
        var connector = context.getConnectorFactory().createConnector(endpoint);
        return attachConnector(connector);
    }

    @Override
    public ConnectorAttachment attachConnector(String endpoint, ConnectorResolver resolver) {
        var connector = context.getConnectorFactory().createConnector(endpoint, resolver);
        return attachConnector(connector);
    }

    @Override
    public ConnectorAttachment attachConnector(String endpoint, ConnectorContext connectorContext) {
        var connector = context.getConnectorFactory().createConnector(endpoint, connectorContext);
        return attachConnector(connector);
    }

    @Override
    public ConnectorAttachment attachConnector(String endpoint, ConnectorResolver resolver,
            ConnectorContext connectorContext) {
        var connector = context.getConnectorFactory().createConnector(endpoint, resolver, connectorContext);
        return attachConnector(connector);
    }

    @Override
    public ConnectorAttachment attachConnector(Connector connector) {
        var connectorAttachment = new DefaultConnectorAttachment(this, connector);
        connectorAttachments.add(connectorAttachment);
        subscribeConnector(connectorAttachment);
        publish(LifecycleType.CONNECTOR_ATTACHED, connector);
        return connectorAttachment;
    }

    private void subscribeConnector(ConnectorAttachment connectorAttachment) {
        connectorAttachment.getConnector().getConsumer().ifPresent(consumer -> subscribeConsumer(connectorAttachment, consumer));
    }

    private void subscribeConsumer(ConnectorAttachment connectorAttachment, Consumer consumer) {
        consumer.subscribe((msg, deferred) -> publish(connectorAttachment, msg, deferred));
    }

    protected void publish(ConnectorAttachment connectorAttachment, Message msg, Deferred<Message, Exception> deferred) {
        var processedMessage = preprocessMessage(connectorAttachment, msg);
        var processedDeferred = preprocessDeferred(connectorAttachment, deferred);
        var routingContext = new DefaultRoutingContext(this, processedMessage, processedDeferred);
        handleMessages(routingContext);
        subject.onNext(routingContext);
    }

    private void handleMessages(RoutingContext rc) {
        if (policyEnforcers.length == 0 && !subject.hasObservers()) {
            rc.getDeferred().reject(new NoSubscriberException());
            return;
        }
        var predicateContext = new PredicateContext(rc.getMessage());
        for (var enforcer : policyEnforcers) {
            enforcer.execute(rc, context, predicateContext);
        }
    }

    private Message preprocessMessage(ConnectorAttachment connectorAttachment, Message message) {
        if (connectorAttachment == null)
            return message;
        var transformer = connectorAttachment.getIncomingTransformer();
        if (transformer == null)
            return message;
        return transformer.transform(message);
    }

    private Deferred<Message, Exception> preprocessDeferred(ConnectorAttachment connectorAttachment,
            Deferred<Message, Exception> deferred) {
        if (connectorAttachment == null)
            return deferred;
        var transformer = connectorAttachment.getOutgoingTransformer();
        if (transformer == null)
            return deferred;
        var processedDeferred = new CompletableDeferredObject<Message, Exception>();
        processedDeferred.map(transformer::transform).forward(deferred);
        return processedDeferred;
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

        for (ConnectorAttachment connectorAttachment : connectorAttachments) {
            connectorAttachment.getConnector().start();
            publish(LifecycleType.CONNECTOR_STARTED, connectorAttachment.getConnector());
        }
        publish(LifecycleType.GATEWAY_STARTED, this);
    }

    @Override
    protected void onStop() {
        for (ConnectorAttachment connectorAttachment : connectorAttachments) {
            connectorAttachment.getConnector().stop();
            publish(LifecycleType.CONNECTOR_STOPPED, connectorAttachment.getConnector());
        }
        publish(LifecycleType.GATEWAY_STOPPED, this);
    }

    @Override
    protected String generateName() {
        return "gateway." + name;
    }
}
