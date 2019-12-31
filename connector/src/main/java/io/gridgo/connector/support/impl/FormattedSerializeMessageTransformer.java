package io.gridgo.connector.support.impl;

import io.gridgo.connector.support.MessageTransformer;
import io.gridgo.framework.support.Message;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FormattedSerializeMessageTransformer implements MessageTransformer {

    private final String format;

    @Override
    public Message transform(Message msg) {
        if (msg == null || msg.body() == null)
            return null;
        return Message.ofAny(msg.headers(), msg.body().toBytes(format));
    }
}
