package io.gridgo.connector.support.impl;

import io.gridgo.bean.BElement;
import io.gridgo.connector.support.MessageTransformer;
import io.gridgo.framework.support.Message;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FormattedDeserializeMessageTransformer implements MessageTransformer {

    private final String format;

    @Override
    public Message transform(Message msg) {
        if (msg == null || msg.body() == null)
            return msg;
        if (!msg.body().isValue()) {
            throw new IllegalArgumentException("Message must be in byte[] format to be used with this transformer");
        }
        return Message.ofAny(msg.headers(), BElement.ofBytes(msg.body().asValue().getRaw(), format));
    }
}
