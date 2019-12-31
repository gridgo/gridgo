package io.gridgo.connector.test;

import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.AsyncDeferredObject;

import io.gridgo.connector.impl.AbstractConsumer;
import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.framework.support.Message;

public class TestConsumer extends AbstractConsumer {

    public TestConsumer(ConnectorContext context) {
        super(context);
    }

    @Override
    protected String generateName() {
        return "consumer.test";
    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onStop() {

    }

    public Promise<Message, Exception> testPublish(Message msg) {
        var deferred = new AsyncDeferredObject<Message, Exception>();
        publish(msg, deferred);
        return deferred.promise();
    }
}
