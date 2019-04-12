package io.gridgo.framework.execution.impl;

import io.gridgo.framework.execution.ExecutionStrategyInstrumenter;

public class WrappedExecutionStrategyInstrumenter implements ExecutionStrategyInstrumenter {

    private ExecutionStrategyInstrumenter[] instrumenters;

    public WrappedExecutionStrategyInstrumenter(ExecutionStrategyInstrumenter... instrumenters) {
        this.instrumenters = instrumenters;
    }

    @Override
    public Runnable instrument(Runnable runnable) {
        var instrumented = runnable;
        for (var instrumenter : instrumenters) {
            instrumented = instrumenter.instrument(instrumented);
        }
        return instrumented;
    }
}
