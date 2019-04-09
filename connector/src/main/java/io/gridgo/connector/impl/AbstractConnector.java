package io.gridgo.connector.impl;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import io.gridgo.connector.Connector;
import io.gridgo.connector.Consumer;
import io.gridgo.connector.Producer;
import io.gridgo.connector.support.config.ConnectorConfig;
import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.framework.impl.AbstractComponentLifecycle;
import lombok.Getter;

public abstract class AbstractConnector extends AbstractComponentLifecycle implements Connector {

    protected static final String LOCALHOST = "localhost";

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    @Getter
    private ConnectorConfig connectorConfig;

    @Getter
    private ConnectorContext context;

    protected Optional<Consumer> consumer = Optional.empty();

    protected Optional<Producer> producer = Optional.empty();

    @Override
    protected String generateName() {
        return "connector." + connectorConfig.getNonQueryEndpoint();
    }

    @Override
    public final Optional<Consumer> getConsumer() {
        return consumer;
    }

    protected String getParam(String name) {
        Object value = connectorConfig.getParameters().get(name);
        return value != null ? value.toString() : null;
    }

    protected String getParam(String name, String defaultValue) {
        Object value = connectorConfig.getParameters().getOrDefault(name, defaultValue);
        return value != null ? value.toString() : null;
    }

    protected String getPlaceholder(String name) {
        return connectorConfig.getPlaceholders().getProperty(name);
    }

    @Override
    public final Optional<Producer> getProducer() {
        return producer;
    }

    @Override
    public final Connector initialize(ConnectorConfig config, ConnectorContext context) {
        if (initialized.compareAndSet(false, true)) {
            this.context = context;
            this.connectorConfig = config;
            this.onInit();
            return this;
        }
        throw new IllegalStateException("Cannot re-init connector of type " + this.getClass().getName());
    }

    protected void onInit() {
        // do nothing
    }

    @Override
    protected void onStart() {
        this.consumer.ifPresent(Consumer::start);
        this.producer.ifPresent(Producer::start);
    }

    @Override
    protected void onStop() {
        this.consumer.ifPresent(Consumer::stop);
        this.producer.ifPresent(Producer::stop);
    }
}
