package io.gridgo.core.support.impl;

import java.util.function.UnaryOperator;

import io.gridgo.core.Gateway;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.framework.support.Message;

public class ForwardComponent extends AbstractTransformableComponent {

    private UnaryOperator<Message> responseTransformer = UnaryOperator.identity();

    public ForwardComponent(String source, String target) {
        super(source, target, false);
    }

    public ForwardComponent(String source, String target, UnaryOperator<Message> transformer) {
        super(source, target, transformer, false);
    }

    public ForwardComponent(String source, String target, UnaryOperator<Message> transformer,
            UnaryOperator<Message> responseTransformer) {
        super(source, target, transformer, false);
        this.responseTransformer = responseTransformer;
    }

    @Override
    protected void doHandle(Gateway target, RoutingContext rc) {
        target.call(rc.getMessage()) //
              .<Message, Exception>filterDone(responseTransformer::apply) //
              .forward(rc.getDeferred());
    }

    @Override
    protected String generateName() {
        return "component.bridge." + getSource() + "." + getTarget();
    }
}
