package io.gridgo.core.support.template.impl;

import org.joo.promise4j.DoneCallback;
import org.joo.promise4j.FailCallback;
import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.JoinedResults;
import org.joo.promise4j.impl.SimpleFailurePromise;

import java.util.List;
import java.util.function.Function;

import io.gridgo.connector.Connector;
import io.gridgo.connector.Producer;
import io.gridgo.core.support.subscription.ConnectorAttachment;
import io.gridgo.core.support.template.ProducerTemplate;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.impl.MultipartMessage;

public abstract class AbstractProducerTemplate implements ProducerTemplate {

    @Override
    public void send(List<ConnectorAttachment> connectors, Message message) {
        for (var connector : connectors) {
            if (match(connector.getConnector(), message))
                send(connector.getConnector(), message);
        }
    }

    @Override
    public void call(List<ConnectorAttachment> connectors, Message message, DoneCallback<Message> doneCallback,
            FailCallback<Exception> failCallback) {
        for (var connector : connectors) {
            if (match(connector.getConnector(), message))
                call(connector.getConnector(), message).done(doneCallback).fail(failCallback);
        }
    }

    protected Promise<Message, Exception> call(Connector connector, Message message) {
        return executeProducerWithMapper(connector, p -> p.call(message));
    }

    protected void send(Connector connector, Message message) {
        connector.getProducer().ifPresent(p -> p.send(message));
    }

    protected Promise<Message, Exception> sendWithAck(Connector connector, Message message) {
        return executeProducerWithMapper(connector, p -> p.sendWithAck(message));
    }

    protected Message convertJoinedResult(JoinedResults<Message> results) {
        return new MultipartMessage(results);
    }

    protected boolean match(Connector connector, Message message) {
        return true;
    }

    protected int findConnectorWithCallSupport(List<ConnectorAttachment> connectors) {
        for (int i = 0; i < connectors.size(); i++) {
            var connector = connectors.get(i);
            if (isCallSupported(connector.getConnector())) {
                return i;
            }
        }
        return -1;
    }

    protected boolean isCallSupported(Connector connector) {
        return connector.getProducer().map(Producer::isCallSupported).orElse(false);
    }

    protected boolean isSendWithAckSupported(Connector connector) {
        return connector.getProducer().map(Producer::isSendWithAckSupported).orElse(false);
    }

    private Promise<Message, Exception> executeProducerWithMapper(Connector connector,
            Function<Producer, Promise<Message, Exception>> mapper) {
        return connector.getProducer() //
                        .map(mapper) //
                        .orElse(createProducerNotFoundPromise(connector.getName()));
    }

    private SimpleFailurePromise<Message, Exception> createProducerNotFoundPromise(String name) {
        return new SimpleFailurePromise<>(
                new UnsupportedOperationException("No producer found for this connector " + name));
    }
}
