package io.gridgo.core;

import io.gridgo.core.support.RoutingContext;

public interface Processor {

	public void process(RoutingContext rc, GridgoContext gc);
}
