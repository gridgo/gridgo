package io.gridgo.core.support.impl;

import java.util.function.UnaryOperator;

import io.gridgo.core.Gateway;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.framework.support.Message;

public class SwitchComponent extends AbstractTransformableComponent {

    public SwitchComponent(String source, String target) {
        super(source, target);
    }

    public SwitchComponent(String source, String target, boolean autoResolve) {
        super(source, target, autoResolve);
    }

    public SwitchComponent(String source, String target, UnaryOperator<Message> transformer) {
        super(source, target, transformer);
    }

    public SwitchComponent(String source, String target, UnaryOperator<Message> transformer, boolean autoResolve) {
        super(source, target, transformer, autoResolve);
    }

    @Override
    protected void doHandle(Gateway target, RoutingContext rc) {
        target.push(rc.getMessage());
    }

    @Override
    protected String generateName() {
        return "component.switch." + getSource() + "." + getTarget();
    }
}
