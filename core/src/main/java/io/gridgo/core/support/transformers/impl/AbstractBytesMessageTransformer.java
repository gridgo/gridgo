package io.gridgo.core.support.transformers.impl;

import io.gridgo.core.support.transformers.BodyTransformer;
import io.gridgo.core.support.transformers.MessageTransformer;
import io.gridgo.framework.support.Message;

public abstract class AbstractBytesMessageTransformer implements MessageTransformer, BodyTransformer {

    @Override
    public Message transform(Message msg) {
        if (msg == null || msg.body() == null || msg.body().isNullValue())
            return msg;
        if (!msg.body().isValue()) {
            throw new IllegalArgumentException("Message must be in byte[] format to be used with this transformer");
        }
        var body = msg.body().asValue().getRaw();
        return doTransform(msg, body);
    }

    protected abstract Message doTransform(Message msg, byte[] body);
}
