package io.gridgo.framework.execution;

import java.util.function.Function;

import org.joo.promise4j.Deferred;
import org.joo.promise4j.Promise;

import io.gridgo.framework.support.Message;

public interface ExecutionStrategyInstrumenter {

    public Runnable instrument(Message msg, Deferred<Message, Exception> deferred, Runnable runnable);

    public default Promise<Message, Exception> instrument(Message msg,
            Function<Message, Promise<Message, Exception>> supplier) {
        throw new UnsupportedOperationException();
    }
}
