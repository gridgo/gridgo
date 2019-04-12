package io.gridgo.connector.test.support;

import java.util.Optional;

import io.gridgo.connector.impl.AbstractConnector;
import io.gridgo.connector.support.annotations.ConnectorEndpoint;

@ConnectorEndpoint(scheme = "test,test1", syntax = "{type}:{transport}://{host}:{port}")
public class TestConnector extends AbstractConnector {

    public String getParamPublic(String name) {
        return getParam(name);
    }

    public String getParamPublic(String name, String defaultValue) {
        return getParam(name, defaultValue);
    }

    public String getPlaceholderPublic(String name) {
        return getPlaceholder(name);
    }

    @Override
    protected void onInit() {
        this.consumer = Optional.of(new TestConsumer(getContext()));
        this.producer = Optional.of(new TestProducer(getContext()));
    }

    @Override
    protected void onStart() {
        if (consumer.isPresent())
            consumer.get().start();
        if (producer.isPresent())
            producer.get().start();
    }

    @Override
    protected void onStop() {
        if (consumer.isPresent())
            consumer.get().stop();
        if (producer.isPresent())
            producer.get().stop();
    }
}
