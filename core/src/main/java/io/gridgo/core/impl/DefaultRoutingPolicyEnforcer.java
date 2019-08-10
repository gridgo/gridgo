package io.gridgo.core.impl;

import io.gridgo.core.GridgoContext;
import io.gridgo.core.RoutingPolicyEnforcer;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.core.support.subscription.RoutingPolicy;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.MessageConstants;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultRoutingPolicyEnforcer implements RoutingPolicyEnforcer {

    private RoutingPolicy policy;

    public DefaultRoutingPolicyEnforcer(final @NonNull RoutingPolicy policy) {
        this.policy = policy;
    }

    @Override
    public void execute(RoutingContext rc, GridgoContext gc, Message context) {
        if (!isMatch(context))
            return;
        var runnable = buildRunnable(rc, gc);
        var instrumenter = policy.getInstrumenter();
        if (instrumenter.isPresent() && isMatchInstrumenter(context))
            runnable = instrumenter.get().instrument(rc.getMessage(), rc.getDeferred(), runnable);
        execute(runnable, rc.getMessage());
    }

    private void execute(Runnable runnable, Message message) {
        policy.getStrategy().ifPresentOrElse(s -> s.execute(runnable, message), runnable);
    }

    private Runnable buildRunnable(RoutingContext rc, GridgoContext gc) {
        return () -> {
            try {
                policy.getProcessor().process(rc, gc);
            } catch (Exception ex) {
                handleException(rc, ex);
            }
        };
    }

    private void handleException(RoutingContext rc, Exception ex) {
        if (log.isErrorEnabled()) {
            var msg = rc.getMessage();
            log.error("Exception caught while executing processor with message id {} and source {}",
                    msg.getPayload().getId().orElse(null), //
                    msg.getMisc().get(MessageConstants.SOURCE), ex);
        }
        if (rc.getDeferred() != null)
            rc.getDeferred().reject(ex);
    }

    private boolean isMatch(Message context) {
        return policy.getCondition().map(c -> c.test(context)).orElse(true);
    }

    private boolean isMatchInstrumenter(Message context) {
        return policy.getInstrumenterCondition().map(c -> c.test(context)).orElse(true);
    }
}
