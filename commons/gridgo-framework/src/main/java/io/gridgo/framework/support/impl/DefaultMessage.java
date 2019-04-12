package io.gridgo.framework.support.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.gridgo.bean.BValue;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.Payload;
import lombok.Getter;

@Getter
public class DefaultMessage implements Message {

    private final Map<String, Object> misc = new HashMap<>();
    private Optional<BValue> routingId = Optional.empty();
    private Payload payload;

    public DefaultMessage() {

    }

    public DefaultMessage(Payload payload) {
        this.payload = payload;
    }

    public DefaultMessage(BValue routingId, Payload payload) {
        this.routingId = Optional.of(routingId);
        this.payload = payload;
    }

    public DefaultMessage(BValue routingId, Map<String, Object> misc, Payload payload) {
        this.routingId = Optional.of(routingId);
        this.payload = payload;
        if (misc != null) {
            this.misc.putAll(misc);
        }
    }

    public DefaultMessage(Map<String, Object> misc, Payload payload) {
        this.payload = payload;
        if (misc != null) {
            this.misc.putAll(misc);
        }
    }

    @Override
    public Message addMisc(String key, Object value) {
        this.misc.put(key, value);
        return this;
    }

    @Override
    public Message setRoutingId(BValue routingId) {
        this.routingId = Optional.ofNullable(routingId);
        return this;
    }

    protected Message setPayload(Payload payload) {
        this.payload = payload;
        return this;
    }
}
