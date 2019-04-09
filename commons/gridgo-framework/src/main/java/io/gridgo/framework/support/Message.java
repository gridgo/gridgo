package io.gridgo.framework.support;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Optional;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BValue;
import io.gridgo.framework.support.impl.DefaultMessage;
import io.gridgo.framework.support.impl.MultipartMessage;
import io.gridgo.utils.wrapper.ByteBufferInputStream;

public interface Message {

    public Payload getPayload();

    public default BElement body() {
        return getPayload().getBody();
    }

    public default BObject headers() {
        return getPayload().getHeaders();
    }

    public Map<String, Object> getMisc();
    
    public default Object getMisc(String key) {
        return getMisc().get(key);
    }

    public Message addMisc(String key, Object value);

    /**
     * routingId use for *-to-1 communication like duplex socket. RoutingId indicate
     * which endpoint will be the target
     * 
     * @return the routing id
     */
    public Optional<BValue> getRoutingId();

    public Message setRoutingId(BValue routingId);

    public default Message setRoutingIdFromAny(Object routingId) {
        this.setRoutingId(BValue.of(routingId));
        return this;
    }

    public default Message attachTraceId(String traceId) {
        headers().setAny(MessageConstants.TRACE_ID, traceId);
        return this;
    }

    public default String getTraceId() {
        return headers().getString(MessageConstants.TRACE_ID);
    }

    public default Message copyTraceId(Message source) {
        return attachTraceId(source.getTraceId());
    }

    public default Message attachSource(String name) {
        if (name != null)
            getMisc().putIfAbsent(MessageConstants.SOURCE, name);
        return this;
    }

    public default String getSource() {
        var source = getMisc().get(MessageConstants.SOURCE);
        return source != null ? source.toString() : MessageConstants.NO_NAMED;
    }

    static Message ofEmpty() {
        return of(Payload.ofEmpty());
    }

    static Message ofAny(Object body) {
        return of(Payload.of(BElement.ofAny(body)));
    }

    static Message ofAny(BObject headers, Object body) {
        return of(Payload.of(headers, BElement.ofAny(body)));
    }

    static Message of(Payload payload) {
        return new DefaultMessage(payload);
    }

    static Message of(BValue routingId, Payload payload) {
        return new DefaultMessage(routingId, payload);
    }

    static Message of(BValue routingId, Map<String, Object> misc, Payload payload) {
        return new DefaultMessage(routingId, misc, payload);
    }

    static Message parse(byte[] bytes) {
        return parse(ByteBuffer.wrap(bytes));
    }

    static Message parse(ByteBuffer buffer) {
        return parse(new ByteBufferInputStream(buffer));
    }

    static Message parse(InputStream inputStream) {
        BElement data = BElement.ofBytes(inputStream);
        return parse(data);
    }

    static Message parse(BElement data) {
        Payload payload = null;

        var multipart = false;

        if (data instanceof BArray && data.asArray().size() == 3) {
            BArray arr = data.asArray();
            BElement id = arr.get(0);

            BElement headers = arr.get(1);
            if (headers.isValue() && headers.asValue().isNull()) {
                headers = BObject.ofEmpty();
            }
            multipart = headers.asObject().getBoolean(MessageConstants.IS_MULTIPART, false);

            BElement body = arr.get(2);
            if (body.isValue() && body.asValue().isNull()) {
                body = null;
            }

            if (id.isValue() && headers.isObject()) {
                payload = Payload.of(id.asValue(), headers.asObject(), body);
            }
        }

        if (payload == null) {
            payload = Payload.of(data);
        }

        if (multipart)
            return new MultipartMessage(payload);

        return Message.of(payload);
    }
}
