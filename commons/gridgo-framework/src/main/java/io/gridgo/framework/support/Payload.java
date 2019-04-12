package io.gridgo.framework.support;

import java.util.Optional;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BValue;
import io.gridgo.framework.support.impl.DefaultPayload;

public interface Payload {

    public Optional<BValue> getId();

    public BObject getHeaders();

    public BElement getBody();

    /**
     * add a header record
     * 
     * @param key   header record's key
     * @param value header record's value
     * @return headers object
     */
    public Payload addHeader(String key, Object value);

    public Payload addHeaderIfAbsent(String key, Object value);

    public Payload setBody(BElement body);

    public Payload setId(Optional<BValue> id);

    public Payload setIdFromAny(Object id);

    public default BArray toBArray() {
        return BArray.ofSequence(this.getId().orElse(null), this.getHeaders(), this.getBody());
    }

    public static Payload of(BValue id, BElement body) {
        return new DefaultPayload(Optional.of(id), body);
    }

    public static Payload of(BValue id, BObject headers, BElement body) {
        return new DefaultPayload(Optional.of(id), headers, body);
    }

    public static Payload of(BObject headers, BElement body) {
        return new DefaultPayload(headers, body);
    }

    public static Payload of(BElement body) {
        return new DefaultPayload(body);
    }

    public static Payload ofEmpty() {
        return of(null);
    }
}
