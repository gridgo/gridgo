package io.gridgo.connector.test.support;

import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.CompletableDeferredObject;

import io.gridgo.bean.BValue;
import io.gridgo.connector.impl.AbstractProducer;
import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.Payload;

public class TestProducer extends AbstractProducer {

    protected TestProducer(ConnectorContext context) {
        super(context);
    }

    @Override
    public Promise<Message, Exception> call(Message request) {
        var deferred = new CompletableDeferredObject<Message, Exception>();
        int body = request.body().asValue().getInteger();
        var message = Message.of(Payload.of(BValue.of(body + 1)));
        ack(deferred, message, null);
        return deferred.promise();
    }

    @Override
    protected String generateName() {
        return "producer.test";
    }

    @Override
    public boolean isCallSupported() {
        return true;
    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onStop() {

    }

    @Override
    public void send(Message message) {

    }

    @Override
    public Promise<Message, Exception> sendWithAck(Message message) {
        var deferred = new CompletableDeferredObject<Message, Exception>();
        ack(deferred, null, new RuntimeException("test exception"));
        return deferred.promise();
    }
}
