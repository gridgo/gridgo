package io.gridgo.core.impl;

import org.joo.libra.PredicateContext;

import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.core.support.subscription.RoutingPolicy;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RoutingPolicyEnforcer {

    private RoutingPolicy policy;

    public RoutingPolicyEnforcer(final @NonNull RoutingPolicy policy) {
        this.policy = policy;
    }

    public void execute(RoutingContext rc, GridgoContext gc, PredicateContext context) {
        if (!isMatch(context))
            return;
        var runnable = buildRunnable(rc, gc);
        var instrumenter = policy.getInstrumenter();
        if (instrumenter.isPresent() && isMatchInstrumenter(context))
            runnable = instrumenter.get().instrument(runnable);
        execute(runnable);
    }

    private void execute(Runnable runnable) {
        policy.getStrategy().ifPresentOrElse(s -> s.execute(runnable), runnable);
    }

    private Runnable buildRunnable(RoutingContext rc, GridgoContext gc) {
        return () -> {
            try {
                doProcess(rc, gc);
            } catch (Exception ex) {
                log.error("Exception caught while executing processor", ex);
                if (rc.getDeferred() != null)
                    rc.getDeferred().reject(ex);
            }
        };
    }

    private boolean isMatch(PredicateContext context) {
        return policy.getCondition().map(c -> c.satisfiedBy(context)).orElse(true);
    }

    private boolean isMatchInstrumenter(PredicateContext context) {
        return policy.getInstrumenterCondition().map(c -> c.satisfiedBy(context)).orElse(true);
    }

    private void doProcess(RoutingContext rc, GridgoContext gc) {
        policy.getProcessor().process(rc, gc);
    }
}
