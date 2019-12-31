package io.gridgo.connector.support;

import io.gridgo.framework.support.Message;

public interface MessageTransformer {

    public Message transform(Message msg);
}
