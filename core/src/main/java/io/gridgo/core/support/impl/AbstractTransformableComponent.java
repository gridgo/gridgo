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

    public AbstractTransformableComponent(String source, String target) {
        super(source, target);
    }

    public AbstractTransformableComponent(String source, String target, UnaryOperator<Message> transformer) {
        super(source, target);
        this.transformer = Optional.ofNullable(transformer);
    }

    @Override
    protected void startWithGateways(Gateway source, Gateway target) {
        this.disposable = source.asObservable().map(this::transform).forEach(msg -> handle(target, msg));
    }

    protected Message transform(RoutingContext rc) {
        return transformer.orElse(Function.identity()).apply(rc.getMessage());
    }

    protected abstract void handle(Gateway target, Message message);

    @Override
    protected void onStop() {
        if (this.disposable != null)
            this.disposable.dispose();
    }
}
