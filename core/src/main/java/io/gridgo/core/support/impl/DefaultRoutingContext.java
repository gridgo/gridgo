package io.gridgo.core.support.impl;

import org.joo.promise4j.Deferred;

import io.gridgo.core.Gateway;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.framework.support.Message;
import lombok.Getter;

@Getter
public class DefaultRoutingContext implements RoutingContext {

    private Gateway caller;

    private Message message;

    private Deferred<Message, Exception> deferred;

    public DefaultRoutingContext(Gateway caller, Message msg, Deferred<Message, Exception> deferred) {
        this.caller = caller;
        this.message = msg;
        this.deferred = deferred;
    }
}
