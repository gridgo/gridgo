package io.gridgo.core.support;

import org.joo.promise4j.Deferred;

import io.gridgo.core.Gateway;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.MessageConstants;

public interface RoutingContext {

    public Message getMessage();

    public Gateway getCaller();

    public default String getCreator() {
        var msg = getMessage();
        if (msg == null || msg.getMisc() == null || !msg.getMisc().containsKey(MessageConstants.CREATOR))
            return null;
        return msg.getMisc().get(MessageConstants.CREATOR).toString();
    }

    public Deferred<Message, Exception> getDeferred();
}
