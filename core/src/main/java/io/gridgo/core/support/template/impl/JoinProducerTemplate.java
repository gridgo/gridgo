package io.gridgo.core.support.template.impl;

import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.SimpleDonePromise;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import io.gridgo.core.support.subscription.ConnectorAttachment;
import io.gridgo.framework.support.Message;

public class JoinProducerTemplate extends AbstractProducerTemplate {

    @Override
    public Promise<Message, Exception> sendWithAck(List<ConnectorAttachment> connectors, Message message) {
        return executeProducerWithMapper(connectors, this::isSendWithAckSupported, c -> sendWithAck(c, message));
    }

    @Override
    public Promise<Message, Exception> call(List<ConnectorAttachment> connectors, Message message) {
        return executeProducerWithMapper(connectors, this::isCallSupported, c -> call(c, message));
    }

    private Promise<Message, Exception> executeProducerWithMapper(List<ConnectorAttachment> connectors,
            Predicate<ConnectorAttachment> predicate, Function<ConnectorAttachment, Promise<Message, Exception>> mapper) {
        var promises = new ArrayList<Promise<Message, Exception>>();
        connectors.stream()
                  .filter(predicate)
                  .map(mapper)
                  .forEach(promises::add);
        if (promises.isEmpty())
            return new SimpleDonePromise<>(null);
        return Promise.all(promises)
                      .map(this::convertJoinedResult);
    }
}
