package io.gridgo.framework.support.impl;

import java.util.Optional;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BValue;
import io.gridgo.framework.support.Payload;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class DefaultPayload implements Payload {

    private Optional<BValue> id = Optional.empty();

    private BObject headers;

    private BElement body;

    public DefaultPayload(Optional<BValue> id, final @NonNull BObject headers, BElement body) {
        this.id = id;
        this.headers = headers;
        this.body = body;
    }

    public DefaultPayload(final @NonNull BObject headers, BElement body) {
        this.headers = headers;
        this.body = body;
    }

    public DefaultPayload(Optional<BValue> id, BElement body) {
        this.id = id;
        this.body = body;
        this.headers = BObject.ofEmpty();
    }

    public DefaultPayload(BElement body) {
        this.body = body;
        this.headers = BObject.ofEmpty();
    }

    @Override
    public Payload setBody(BElement body) {
        this.body = body;
        return this;
    }

    @Override
    public Payload setIdFromAny(Object id) {
        this.id = Optional.of(BValue.of(id));
        return this;
    }

    @Override
    public Payload setId(Optional<BValue> id) {
        this.id = id;
        return this;
    }

    @Override
    public Payload addHeader(String key, Object value) {
        this.headers.setAny(key, value);
        return this;
    }

    @Override
    public Payload addHeaderIfAbsent(String key, Object value) {
        this.headers.setAnyIfAbsent(key, value);
        return this;
    }
}
