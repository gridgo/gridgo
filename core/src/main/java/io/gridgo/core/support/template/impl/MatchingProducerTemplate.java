package io.gridgo.core.support.template.impl;

import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.JoinedPromise;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

import io.gridgo.connector.Connector;
import io.gridgo.core.support.subscription.ConnectorAttachment;
import io.gridgo.framework.support.Message;
import lombok.NonNull;

public class MatchingProducerTemplate extends AbstractProducerTemplate {

    private BiPredicate<Connector, Message> predicate;

    public MatchingProducerTemplate(final @NonNull BiPredicate<Connector, Message> predicate) {
        this.predicate = predicate;
    }

    @Override
    public Promise<Message, Exception> sendWithAck(List<ConnectorAttachment> connectors, Message message) {
        return executeProducerWithMapper(connectors, message, c -> sendWithAck(c, message));
    }

    @Override
    public Promise<Message, Exception> call(List<ConnectorAttachment> connectors, Message message) {
        return executeProducerWithMapper(connectors, message, c -> call(c, message));
    }

    @Override
    protected boolean match(ConnectorAttachment connector, Message message) {
        return predicate.test(connector.getConnector(), message);
    }

    private Promise<Message, Exception> executeProducerWithMapper(List<ConnectorAttachment> connectors, Message message,
            Function<ConnectorAttachment, Promise<Message, Exception>> mapper) {
        var promises = new ArrayList<Promise<Message, Exception>>();
        connectors.stream()
                  .filter(c -> match(c, message))
                  .map(mapper)
                  .forEach(promises::add);
        return JoinedPromise.from(promises).filterDone(this::convertJoinedResult);
    }
}
