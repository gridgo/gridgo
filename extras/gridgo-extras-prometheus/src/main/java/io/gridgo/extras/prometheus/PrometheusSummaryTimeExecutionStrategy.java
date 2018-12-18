package io.gridgo.extras.prometheus;

import io.gridgo.framework.execution.ExecutionStrategy;
import io.gridgo.framework.execution.impl.WrappedExecutionStrategy;
import io.prometheus.client.Summary;
import lombok.Getter;

public class PrometheusSummaryTimeExecutionStrategy extends WrappedExecutionStrategy {

    @Getter
    private Summary summary;

    public PrometheusSummaryTimeExecutionStrategy(String name, String help) {
        this.summary = Summary.build(name, help).register();
    }

    public PrometheusSummaryTimeExecutionStrategy(Summary summary) {
        this.summary = summary;
    }

    public PrometheusSummaryTimeExecutionStrategy(ExecutionStrategy strategy, String name, String help) {
        super(strategy);
        this.summary = Summary.build(name, help).register();
    }

    public PrometheusSummaryTimeExecutionStrategy(ExecutionStrategy strategy, Summary summary) {
        super(strategy);
        this.summary = summary;
    }

    @Override
    protected void wrap(Runnable runnable) {
        summary.time(runnable);
    }
}
