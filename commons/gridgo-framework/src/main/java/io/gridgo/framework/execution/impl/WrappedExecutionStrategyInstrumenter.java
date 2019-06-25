package io.gridgo.framework.execution.impl;

import org.joo.promise4j.Deferred;

import io.gridgo.framework.execution.ExecutionStrategyInstrumenter;
import io.gridgo.framework.support.Message;

public class WrappedExecutionStrategyInstrumenter implements ExecutionStrategyInstrumenter {

    private ExecutionStrategyInstrumenter[] instrumenters;

    public WrappedExecutionStrategyInstrumenter(ExecutionStrategyInstrumenter... instrumenters) {
        this.instrumenters = instrumenters;
    }

    @Override
    public Runnable instrument(Message msg, Deferred<Message, Exception> deferred, Runnable runnable) {
        var instrumented = runnable;
        for (var instrumenter : instrumenters) {
            instrumented = instrumenter.instrument(msg, deferred, instrumented);
        }
        return instrumented;
    }
}
