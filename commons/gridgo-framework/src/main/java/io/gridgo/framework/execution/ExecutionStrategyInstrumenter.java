package io.gridgo.framework.execution;

public interface ExecutionStrategyInstrumenter {

    public Runnable instrument(Runnable runnable);
}
