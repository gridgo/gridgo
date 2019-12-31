package io.gridgo.core.support.transformers.impl;

import io.gridgo.core.support.transformers.MessageTransformer;
import io.gridgo.framework.support.Message;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FormattedSerializeMessageTransformer implements MessageTransformer {

    private final String format;

    @Override
    public Message transform(Message msg) {
        if (msg == null || msg.body() == null)
            return msg;
        return Message.ofAny(msg.headers(), msg.body().toBytes(format));
    }
}
