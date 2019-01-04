package io.gridgo.core.support.impl;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import io.gridgo.core.Gateway;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.framework.support.Message;
import io.reactivex.disposables.Disposable;
import lombok.Getter;

@Getter
public abstract class AbstractTransformableComponent extends AbstractDirectionalComponent {

    private Optional<Function<Message, Message>> transformer = Optional.empty();

    private Disposable disposable;

    private boolean autoResolve;

    public AbstractTransformableComponent(String source, String target) {
        this(source, target, false);
    }

    public AbstractTransformableComponent(String source, String target, boolean autoResolve) {
        this(source, target, null, autoResolve);
    }

    public AbstractTransformableComponent(String source, String target, UnaryOperator<Message> transformer) {
        this(source, target, transformer, false);
    }

    public AbstractTransformableComponent(String source, String target, UnaryOperator<Message> transformer,
            boolean autoResolve) {
        super(source, target);
        this.transformer = Optional.ofNullable(transformer);
        this.autoResolve = autoResolve;
    }

    @Override
    protected void startWithGateways(Gateway source, Gateway target) {
        this.disposable = source.asObservable().map(this::transform).forEach(rc -> handle(target, rc));
    }

    protected RoutingContext transform(RoutingContext rc) {
        var msg = transformer.orElse(Function.identity()).apply(rc.getMessage());
        return new DefaultRoutingContext(rc.getCaller(), msg, rc.getDeferred());
    }

    protected void handle(Gateway target, RoutingContext rc) {
        try {
            doHandle(target, rc);
            if (autoResolve && rc.getDeferred() != null)
                rc.getDeferred().resolve(null);
        } catch (Exception ex) {
            if (autoResolve && rc.getDeferred() != null)
                rc.getDeferred().reject(ex);
        }
    }

    protected abstract void doHandle(Gateway target, RoutingContext rc);

    @Override
    protected void onStop() {
        if (this.disposable != null)
            this.disposable.dispose();
    }
}
