package io.gridgo.core.support;

import io.gridgo.core.GridgoContext;
import io.gridgo.framework.ComponentLifecycle;

public interface ContextAwareComponent extends ComponentLifecycle {

	public void setContext(GridgoContext context);
}
