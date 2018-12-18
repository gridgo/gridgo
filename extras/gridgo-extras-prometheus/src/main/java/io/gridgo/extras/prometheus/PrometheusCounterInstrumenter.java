package io.gridgo.extras.prometheus;

import io.gridgo.framework.execution.ExecutionStrategyInstrumenter;
import io.prometheus.client.Counter;
import lombok.Getter;

public class PrometheusCounterInstrumenter implements ExecutionStrategyInstrumenter {

    @Getter
    private Counter counter;

    public PrometheusCounterInstrumenter(String name, String help) {
        this.counter = Counter.build().name(name).help(help).register();
    }

    public PrometheusCounterInstrumenter(Counter counter) {
        this.counter = counter;
    }

    @Override
    public Runnable instrument(Runnable runnable) {
        return () -> {
            counter.inc();
            runnable.run();
        };
    }
}
