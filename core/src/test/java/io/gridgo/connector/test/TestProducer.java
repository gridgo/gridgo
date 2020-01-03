package io.gridgo.connector.test;

import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.CompletableDeferredObject;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.connector.impl.AbstractProducer;
import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.framework.support.Message;

public class TestProducer extends AbstractProducer {

    protected TestProducer(ConnectorContext context) {
        super(context);
    }

    @Override
    public Promise<Message, Exception> call(Message request) {
        var deferred = new CompletableDeferredObject<Message, Exception>();
        var json = request.body().asValue().getString();
        var obj = BElement.ofJson(json).asObject();
        var message = Message.ofAny(BObject.of("reply", obj.get("name")));
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
        if (!message.body().isValue())
            throw new RuntimeException("Message body must be value");
        var json = message.body().asValue().getString();
        var obj = BElement.ofJson(json);
        if (!obj.isObject())
            throw new RuntimeException("Message body must be JSON object");
    }

    @Override
    public Promise<Message, Exception> sendWithAck(Message message) {
        return call(message);
    }
}
