package io.gridgo.core.support.impl;

import io.gridgo.core.Gateway;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.ContextAwareComponent;
import io.gridgo.framework.AbstractComponentLifecycle;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class AbstractDirectionalComponent extends AbstractComponentLifecycle implements ContextAwareComponent {

	private String source;

	private String target;

	@Setter
	private GridgoContext context;

	public AbstractDirectionalComponent(String source, String target) {
		this.source = source;
		this.target = target;
	}

	@Override
	protected void onStart() {
		if (context == null)
			throw new IllegalStateException("Context is not set");
		var sourceGateway = context.findGateway(source);
		var targetGateway = context.findGateway(target);
		if (sourceGateway.isEmpty() || targetGateway.isEmpty()) {
			getLogger().warn("Source or target gateway is not available");
			return;
		}
		startWithGateways(sourceGateway.get(), targetGateway.get());
	}

	protected abstract void startWithGateways(Gateway source, Gateway target);
}
