package io.gridgo.core.support.transformers;

import io.gridgo.framework.support.Message;

public interface MessageTransformer {

    public Message transform(Message msg);
}
