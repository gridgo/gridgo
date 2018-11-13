package io.gridgo.core.impl;

import org.joo.libra.PredicateContext;

import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.core.support.subscription.RoutingPolicy;
import lombok.NonNull;

public class RoutingPolicyEnforcer {

	private RoutingPolicy policy;

	public RoutingPolicyEnforcer(final @NonNull RoutingPolicy policy) {
		this.policy = policy;
	}

	public boolean isMatch(PredicateContext context) {
		return policy.getCondition().isEmpty() || policy.getCondition().get().satisfiedBy(context);
	}

	public void execute(RoutingContext rc, GridgoContext gc) {
		policy.getProcessor().process(rc, gc);
	}
}
