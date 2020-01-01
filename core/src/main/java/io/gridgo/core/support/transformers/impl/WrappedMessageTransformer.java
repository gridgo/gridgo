package io.gridgo.core.support.transformers.impl;

import io.gridgo.core.support.transformers.MessageTransformer;
import io.gridgo.framework.support.Message;

public class WrappedMessageTransformer implements MessageTransformer {

    private final MessageTransformer[] transformers;

    public WrappedMessageTransformer(MessageTransformer...transformers) {
        this.transformers = transformers;
    }

    @Override
    public Message transform(Message msg) {
        var result = msg;
        for (MessageTransformer transformer : transformers) {
            result = transformer.transform(result);
        }
        return result;
    }
}
