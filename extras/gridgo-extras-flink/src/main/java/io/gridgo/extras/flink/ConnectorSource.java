package io.gridgo.extras.flink;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

import io.gridgo.connector.Connector;
import io.gridgo.connector.ConnectorFactory;
import io.gridgo.connector.ConnectorResolver;
import io.gridgo.connector.impl.factories.DefaultConnectorFactory;
import io.gridgo.framework.support.Message;

public class ConnectorSource implements SourceFunction<Message> {

    private static final ConnectorFactory DEFAULT_FACTORY = new DefaultConnectorFactory();

    private static final long serialVersionUID = -7568626695827640831L;

    private Connector connector;

    public ConnectorSource(String endpoint) {
        this.connector = DEFAULT_FACTORY.createConnector(endpoint);
    }

    public ConnectorSource(Connector connector) {
        this.connector = connector;
    }

    public ConnectorSource(ConnectorFactory factory, String endpoint) {
        this.connector = factory.createConnector(endpoint);
    }

    public ConnectorSource(ConnectorResolver resolver, String endpoint) {
        this.connector = resolver.resolve(endpoint);
    }

    @Override
    public void run(SourceContext<Message> ctx) throws Exception {
        connector.getConsumer().orElseThrow() //
                 .subscribe((msg, deferred) -> {
                     ctx.collect(msg);
                     deferred.resolve(Message.ofEmpty());
                 });
        connector.start();
    }

    @Override
    public void cancel() {
        connector.stop();
    }
}
