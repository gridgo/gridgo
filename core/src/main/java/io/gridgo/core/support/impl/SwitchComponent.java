package io.gridgo.core.support.impl;

import java.util.function.UnaryOperator;

import io.gridgo.core.Gateway;
import io.gridgo.framework.support.Message;

public class SwitchComponent extends AbstractTransformableComponent {

    public SwitchComponent(String source, String target) {
        super(source, target);
    }

    public SwitchComponent(String source, String target, UnaryOperator<Message> transformer) {
        super(source, target, transformer);
    }

    @Override
    protected void handle(Gateway target, Message message) {
        target.push(message);
    }

    @Override
    protected String generateName() {
        return "component.switch." + getSource() + "." + getTarget();
    }
}
