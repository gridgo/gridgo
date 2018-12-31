package io.gridgo.extras.prometheus;

import io.gridgo.framework.execution.ExecutionStrategyInstrumenter;
import io.prometheus.client.Summary;
import lombok.Getter;

public class PrometheusSummaryTimeInstrumenter implements ExecutionStrategyInstrumenter {

    @Getter
    private Summary summary;

    public PrometheusSummaryTimeInstrumenter(String name, String help) {
        this.summary = Summary.build(name, help).register();
    }

    public PrometheusSummaryTimeInstrumenter(Summary summary) {
        this.summary = summary;
    }

    @Override
    public Runnable instrument(Runnable runnable) {
        return () -> summary.time(runnable);
    }
}
