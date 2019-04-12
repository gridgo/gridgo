package io.gridgo.core.support.config.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import org.joo.promise4j.Deferred;

import io.gridgo.connector.Connector;
import io.gridgo.connector.ConnectorFactory;
import io.gridgo.connector.ConnectorResolver;
import io.gridgo.connector.Consumer;
import io.gridgo.connector.impl.factories.DefaultConnectorFactory;
import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.core.support.exceptions.NoConsumerException;
import io.gridgo.framework.support.Message;

public class ConsumerConfigurator extends AbstractConnectorConfigurator {

    private AtomicBoolean firstTime = new AtomicBoolean(true);

    private ConsumerConfigurator(Connector connector, boolean owned) {
        super(connector, owned);
    }

    public static final ConsumerConfigurator ofConnector(Connector connector) {
        return new ConsumerConfigurator(connector, false);
    }

    public static final ConsumerConfigurator ofEndpoint(String endpoint) {
        var connector = new DefaultConnectorFactory().createConnector(endpoint);
        return new ConsumerConfigurator(connector, true);
    }

    public static final ConsumerConfigurator ofEndpoint(String endpoint, ConnectorFactory factory) {
        var connector = factory.createConnector(endpoint);
        return new ConsumerConfigurator(connector, true);
    }

    public static final ConsumerConfigurator ofEndpoint(String endpoint, ConnectorResolver resolver) {
        var connector = resolver.resolve(endpoint);
        return new ConsumerConfigurator(connector, true);
    }

    public static final ConsumerConfigurator ofEndpoint(String endpoint, ConnectorResolver resolver,
            ConnectorContext context) {
        var connector = resolver.resolve(endpoint, context);
        return new ConsumerConfigurator(connector, true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getConnector().getConsumer() //
                      .ifPresentOrElse(this::resolveWithConsumer, this::onNoConsumer);
    }

    private Consumer resolveWithConsumer(Consumer consumer) {
        return consumer.subscribe(this::publishEvent);
    }

    private void onNoConsumer() {
        publishFailed(new NoConsumerException("No consumer available for connector " + getConnector().getName()));
    }

    private void publishEvent(Message msg, Deferred<Message, Exception> deferred) {
        if (firstTime.compareAndSet(true, false)) {
            publishLoaded(msg.body());
        } else {
            publishReloaded(msg.body());
        }
        deferred.resolve(Message.ofEmpty());
    }

    @Override
    protected String generateName() {
        return "config.consumer." + getConnector().getName();
    }
}
