package io.gridgo.core.support.impl;

import io.gridgo.core.Gateway;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.ContextAwareComponent;
import io.gridgo.framework.impl.AbstractComponentLifecycle;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
public abstract class AbstractDirectionalComponent extends AbstractComponentLifecycle implements ContextAwareComponent {

    private String source;

    private String target;

    @Setter
    private GridgoContext context;

    public AbstractDirectionalComponent(final @NonNull String source, final @NonNull String target) {
        if (source.equals(target))
            throw new IllegalArgumentException("Source and target cannot be the same");
        this.source = source;
        this.target = target;
    }

    @Override
    protected void onStart() {
        if (context == null)
            throw new IllegalStateException("Context is not set");
        var sourceGateway = context.findGatewayMandatory(source);
        var targetGateway = context.findGatewayMandatory(target);
        startWithGateways(sourceGateway, targetGateway);
    }

    protected abstract void startWithGateways(Gateway source, Gateway target);
}
