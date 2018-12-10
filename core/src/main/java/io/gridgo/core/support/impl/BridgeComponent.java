package io.gridgo.core.support.impl;

import java.util.function.Function;

import io.gridgo.core.Gateway;
import io.gridgo.framework.support.Message;

public class BridgeComponent extends AbstractTransformableComponent {

    public BridgeComponent(String source, String target) {
        this(source, target, null);
    }

    public BridgeComponent(String source, String target, Function<Message, Message> transformer) {
        super(source, target, transformer);
    }

    @Override
    protected void handle(Gateway target, Message message) {
        target.send(message);
    }

    @Override
    protected String generateName() {
        return "component.bridge." + getSource() + "." + getTarget();
    }
}
