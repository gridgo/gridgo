package io.gridgo.core.support.transformers.impl;

import io.gridgo.core.support.transformers.BodyTransformer;
import io.gridgo.core.support.transformers.MessageTransformer;
import io.gridgo.framework.support.Message;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FormattedSerializeMessageTransformer implements MessageTransformer, BodyTransformer {

    private final String format;

    @Override
    public Message transform(Message msg) {
        if (msg == null || msg.body() == null)
            return msg;
        return transformBody(msg, msg.body().toBytes(format));
    }
}
