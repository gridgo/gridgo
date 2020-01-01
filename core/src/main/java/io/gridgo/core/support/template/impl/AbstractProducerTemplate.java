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
            if (match(connector, message))
                send(connector, message);
        }
    }

    @Override
    public void call(List<ConnectorAttachment> connectors, Message message, DoneCallback<Message> doneCallback,
            FailCallback<Exception> failCallback) {
        for (var connector : connectors) {
            if (match(connector, message))
                call(connector, message).done(doneCallback).fail(failCallback);
        }
    }

    protected Promise<Message, Exception> call(ConnectorAttachment connectorAttachment, Message message) {
        var processed = prepareMessage(connectorAttachment, message);
        var promise = executeProducerWithMapper(connectorAttachment.getConnector(), p -> p.call(processed));
        return preparePromise(connectorAttachment, promise);
    }

    private Promise<Message, Exception> preparePromise(ConnectorAttachment connectorAttachment,
            Promise<Message, Exception> promise) {
        if (connectorAttachment.getIncomingTransformer() == null)
            return promise;
        return promise.map(connectorAttachment.getIncomingTransformer()::transform);
    }

    protected void send(ConnectorAttachment connectorAttachment, Message message) {
        var processed = prepareMessage(connectorAttachment, message);
        connectorAttachment.getConnector().getProducer().ifPresent(p -> p.send(processed));
    }

    private Message prepareMessage(ConnectorAttachment connectorAttachment, Message message) {
        if (connectorAttachment.getOutgoingTransformer() == null)
            return message;
        return connectorAttachment.getOutgoingTransformer().transform(message);
    }

    protected Promise<Message, Exception> sendWithAck(ConnectorAttachment connectorAttachment, Message message) {
        var processed = prepareMessage(connectorAttachment, message);
        var promise = executeProducerWithMapper(connectorAttachment.getConnector(), p -> p.sendWithAck(processed));
        return preparePromise(connectorAttachment, promise);
    }

    protected Message convertJoinedResult(JoinedResults<Message> results) {
        return new MultipartMessage(results);
    }

    protected boolean match(ConnectorAttachment connectorAttachment, Message message) {
        return true;
    }

    protected int findConnectorWithCallSupport(List<ConnectorAttachment> connectors) {
        for (int i = 0; i < connectors.size(); i++) {
            var connector = connectors.get(i);
            if (isCallSupported(connector)) {
                return i;
            }
        }
        return -1;
    }

    protected boolean isCallSupported(ConnectorAttachment connector) {
        return connector.getConnector().getProducer().map(Producer::isCallSupported).orElse(false);
    }

    protected boolean isSendWithAckSupported(ConnectorAttachment connector) {
        return connector.getConnector().getProducer().map(Producer::isSendWithAckSupported).orElse(false);
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
