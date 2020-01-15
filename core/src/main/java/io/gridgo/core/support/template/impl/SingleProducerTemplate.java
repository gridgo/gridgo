package io.gridgo.core.support.template.impl;

import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.SimpleDonePromise;

import java.util.List;

import io.gridgo.core.support.subscription.ConnectorAttachment;
import io.gridgo.framework.support.Message;

public class SingleProducerTemplate extends AbstractProducerTemplate {

    @Override
    public Promise<Message, Exception> sendWithAck(List<ConnectorAttachment> connectors, Message message) {
        if (connectors.isEmpty())
            return new SimpleDonePromise<>(null);
        var first = connectors.get(0);
        var promise = sendWithAck(first, message);
        for (int i = 1; i < connectors.size(); i++) {
            send(connectors.get(i), message);
        }
        return promise;
    }

    @Override
    public Promise<Message, Exception> call(List<ConnectorAttachment> connectors, Message message) {
        if (connectors.isEmpty())
            return new SimpleDonePromise<>(null);
        var index = findConnectorWithCallSupport(connectors);
        if (index == -1)
            return new SimpleDonePromise<>(null);
        var first = connectors.get(index);
        var promise = call(first, message);
        for (int i = 0; i < connectors.size(); i++) {
            if (i != index)
                send(connectors.get(i), message);
        }
        return promise;
    }
}
