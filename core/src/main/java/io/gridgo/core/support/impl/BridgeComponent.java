package io.gridgo.core.support.impl;

import java.util.function.UnaryOperator;

import io.gridgo.core.Gateway;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.framework.support.Message;

public class BridgeComponent extends AbstractTransformableComponent {

    public BridgeComponent(String source, String target) {
        this(source, target, null);
    }

    public BridgeComponent(String source, String target, UnaryOperator<Message> transformer) {
        super(source, target, transformer);
    }

    @Override
    protected void doHandle(Gateway target, RoutingContext rc) {
        target.send(rc.getMessage());
    }

    @Override
    protected String generateName() {
        return "component.bridge." + getSource() + "." + getTarget();
    }
}
