package io.gridgo.connector.test.support;

import io.gridgo.bean.BObject;
import io.gridgo.connector.impl.AbstractConsumer;
import io.gridgo.connector.support.config.ConnectorContext;

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

    public void testPublish() {
        publish(createMessage(BObject.ofEmpty().setAny("test-header", 1), null), null);
    }
}
