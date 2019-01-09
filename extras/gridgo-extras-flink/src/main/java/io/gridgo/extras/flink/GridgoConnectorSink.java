package io.gridgo.extras.flink;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import io.gridgo.connector.Connector;
import io.gridgo.connector.ConnectorFactory;
import io.gridgo.connector.impl.factories.DefaultConnectorFactory;
import io.gridgo.framework.support.Message;
import io.gridgo.utils.helper.Loggable;

public class GridgoConnectorSink extends RichSinkFunction<Message> implements Loggable {

    private static final ConnectorFactory DEFAULT_FACTORY = new DefaultConnectorFactory();

    private static final long serialVersionUID = -5571127197760351635L;

    private String endpoint;

    private transient Connector connector;

    public GridgoConnectorSink(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void invoke(Message entity) throws Exception {
        connector.getProducer().orElseThrow() //
                 .sendWithAck(entity) //
                 .fail(ex -> getLogger().error("Exception caught while sending message", ex));
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        this.connector = DEFAULT_FACTORY.createConnector(endpoint);
    }

    @Override
    public void close() throws Exception {
        if (connector != null)
            connector.stop();
    }
}
