package io.gridgo.extras.flink;

import java.util.concurrent.locks.LockSupport;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

import io.gridgo.connector.Connector;
import io.gridgo.connector.ConnectorFactory;
import io.gridgo.connector.impl.factories.DefaultConnectorFactory;
import io.gridgo.framework.support.Message;

public class GridgoConnectorSource implements SourceFunction<Message> {

    private static final ConnectorFactory DEFAULT_FACTORY = new DefaultConnectorFactory();

    private static final long serialVersionUID = -7568626695827640831L;

    private transient Connector connector;

    private String endpoint;

    private transient boolean running;

    public GridgoConnectorSource(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void run(SourceContext<Message> ctx) throws Exception {
        connector = DEFAULT_FACTORY.createConnector(endpoint);
        connector.getConsumer().orElseThrow() //
                 .subscribe((msg, deferred) -> {
                     deferred.resolve(Message.ofEmpty());
                     ctx.collect(msg);
                 });
        connector.start();
        idleSpin();
    }

    private void idleSpin() {
        running = true;
        while (running) {
            LockSupport.parkNanos(100L);
        }
    }

    @Override
    public void cancel() {
        running = false;
        if (connector != null)
            connector.stop();
    }
}
