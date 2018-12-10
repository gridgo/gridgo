package io.gridgo.core.support.template.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.JoinedPromise;
import org.joo.promise4j.impl.SimpleDonePromise;

import io.gridgo.connector.Connector;
import io.gridgo.framework.support.Message;

public class JoinProducerTemplate extends AbstractProducerTemplate {

    @Override
    public Promise<Message, Exception> sendWithAck(List<Connector> connectors, Message message) {
        return executeProducerWithMapper(connectors, message, this::isSendWithAckSupported,
                c -> sendWithAck(c, message));
    }

    @Override
    public Promise<Message, Exception> call(List<Connector> connectors, Message message) {
        return executeProducerWithMapper(connectors, message, this::isCallSupported, c -> call(c, message));
    }

    private Promise<Message, Exception> executeProducerWithMapper(List<Connector> connectors, Message message,
            Predicate<Connector> predicate, Function<Connector, Promise<Message, Exception>> mapper) {
        var promises = new ArrayList<Promise<Message, Exception>>();
        connectors.stream().filter(predicate).map(mapper).forEach(promises::add);
        if (promises.isEmpty())
            return new SimpleDonePromise<>(null);
        return JoinedPromise.from(promises).filterDone(this::convertJoinedResult);
    }
}
