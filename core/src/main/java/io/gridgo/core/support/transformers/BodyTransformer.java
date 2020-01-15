package io.gridgo.core.support.transformers;

import io.gridgo.framework.support.Message;

public interface BodyTransformer {

    public default Message transformBody(Message msg, Object newBody) {
        return Message.ofAny(msg.headers(), newBody);
    }
}
