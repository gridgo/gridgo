package io.gridgo.core.support;

import org.joo.promise4j.Deferred;

import io.gridgo.core.Gateway;
import io.gridgo.framework.support.Message;

public interface RoutingContext {

    public Message getMessage();

    public Gateway getCaller();

    public Deferred<Message, Exception> getDeferred();
}
