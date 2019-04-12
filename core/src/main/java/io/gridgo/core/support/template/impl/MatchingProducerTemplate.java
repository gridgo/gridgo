package io.gridgo.core.support.template.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.JoinedPromise;

import io.gridgo.connector.Connector;
import io.gridgo.framework.support.Message;
import lombok.NonNull;

public class MatchingProducerTemplate extends AbstractProducerTemplate {

    private BiPredicate<Connector, Message> predicate;

    public MatchingProducerTemplate(final @NonNull BiPredicate<Connector, Message> predicate) {
        this.predicate = predicate;
    }

    @Override
    public Promise<Message, Exception> sendWithAck(List<Connector> connectors, Message message) {
        return executeProducerWithMapper(connectors, message, c -> sendWithAck(c, message));
    }

    @Override
    public Promise<Message, Exception> call(List<Connector> connectors, Message message) {
        return executeProducerWithMapper(connectors, message, c -> call(c, message));
    }

    @Override
    protected boolean match(Connector connector, Message message) {
        return predicate.test(connector, message);
    }

    private Promise<Message, Exception> executeProducerWithMapper(List<Connector> connectors, Message message,
            Function<Connector, Promise<Message, Exception>> mapper) {
        var promises = new ArrayList<Promise<Message, Exception>>();
        connectors.stream().filter(c -> predicate.test(c, message)).map(mapper).forEach(promises::add);
        return JoinedPromise.from(promises).filterDone(this::convertJoinedResult);
    }
}
