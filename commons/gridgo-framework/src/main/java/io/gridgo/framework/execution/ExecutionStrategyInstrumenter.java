package io.gridgo.framework.execution;

import org.joo.promise4j.Deferred;

import io.gridgo.framework.support.Message;

public interface ExecutionStrategyInstrumenter {

    public Runnable instrument(Message msg, Deferred<Message, Exception> deferred, Runnable runnable);
}
