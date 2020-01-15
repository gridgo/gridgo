package io.gridgo.connector.test;

import java.util.Optional;

import io.gridgo.connector.impl.AbstractConnector;
import io.gridgo.connector.support.annotations.ConnectorEndpoint;

@ConnectorEndpoint(scheme = "testtransformer", syntax = "")
public class TestConnector extends AbstractConnector {

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
