package io.gridgo.connector.impl;

import java.util.Optional;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BValue;
import io.gridgo.framework.impl.AbstractComponentLifecycle;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.Payload;
import lombok.NonNull;

public abstract class AbstractMessageComponent extends AbstractComponentLifecycle {

    /**
     * create a message without payload (message.getPayload() == null) auto id
     * generated
     * 
     * @return the message
     */
    protected Message createMessage() {
        return attachSource(Message.ofEmpty());
    }

    /**
     * create a message with empty payload's header, auto id generated
     * 
     * @param body the body
     * @return the message
     */
    protected Message createMessage(BElement body) {
        return attachSource(createMessage(BObject.ofEmpty(), body));
    }

    /**
     * create a message with empty payload's header, auto id generated
     * 
     * @param body the body
     * @return the message
     */
    protected Message createMessage(Object body) {
        return attachSource(createMessage(BObject.ofEmpty(), BElement.ofAny(body)));
    }

    /**
     * create a message with payload which contains the headers and body, auto id
     * generated
     * 
     * @param headers payload's headers
     * @param body    payload's body
     * @return the message
     */
    protected Message createMessage(@NonNull BObject headers, BElement body) {
        Payload payload = Payload.of(headers, body);
        this.ensurePayloadId(payload);
        return attachSource(Message.of(payload));
    }

    /**
     * check if message not null, message's payload not null, message's payload id
     * is empty, then set message's payload id by value generated from idGenerator
     * if presented
     * 
     * @param message the message where to take payload
     */
    protected void ensurePayloadId(Message message) {
        if (message != null) {
            ensurePayloadId(message.getPayload());
        }
    }

    /**
     * check if payload not null, payload's id is empty, then set payload's id by
     * value generated from idGenerator if presented
     * 
     * @param payload the payload
     */
    protected void ensurePayloadId(Payload payload) {
        if (payload != null && payload.getId().isEmpty()) {
            payload.setId(generateId());
        }
    }

    /**
     * Attach source to the message, making it easier to track.
     * 
     * @param msg the message to be attached with source
     * @return the message itself
     */
    protected Message attachSource(Message msg) {
        return msg.attachSource(getName());
    }

    protected abstract Optional<BValue> generateId();
}
