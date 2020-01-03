package io.gridgo.core.support.template;

import org.joo.promise4j.DoneCallback;
import org.joo.promise4j.FailCallback;
import org.joo.promise4j.Promise;

import java.util.List;

import io.gridgo.core.support.ProducerJoinMode;
import io.gridgo.core.support.subscription.ConnectorAttachment;
import io.gridgo.core.support.template.impl.JoinProducerTemplate;
import io.gridgo.core.support.template.impl.SingleProducerTemplate;
import io.gridgo.framework.support.Message;

public interface ProducerTemplate {

    public static ProducerTemplate create(ProducerJoinMode joinMode) {
        if (joinMode == ProducerJoinMode.JOIN)
            return new JoinProducerTemplate();
        return new SingleProducerTemplate();
    }

    public void send(List<ConnectorAttachment> connectors, Message message);

    public Promise<Message, Exception> sendWithAck(List<ConnectorAttachment> connectors, Message message);

    public Promise<Message, Exception> call(List<ConnectorAttachment> connectors, Message message);

    public void call(List<ConnectorAttachment> connectors, Message message, DoneCallback<Message> doneCallback,
            FailCallback<Exception> failCallback);
}
