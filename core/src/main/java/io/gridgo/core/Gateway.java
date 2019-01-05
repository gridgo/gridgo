package io.gridgo.core;

import org.joo.promise4j.Promise;

import io.gridgo.bean.BObject;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.core.support.Streamable;
import io.gridgo.framework.ComponentLifecycle;
import io.gridgo.framework.support.Message;

public interface Gateway extends ComponentLifecycle, Streamable<RoutingContext> {

    /**
     * Send a message and ignore status and response. This is called fire-and-forget
     * communication. It is not recommended to be used in critical part of the
     * system since message might not be sent successfully and there is no way to
     * know the status.
     * 
     * @param message the message to be sent
     */
    public void send(Message message);

    /**
     * Send a message and wait for acknowledgement but ignore the response. Use this
     * method if you must know the status of the sending.
     * 
     * @param message the message to be sent
     * @return the acknowledgement promise
     */
    public Promise<Message, Exception> sendWithAck(Message message);

    /**
     * Send a message and wait for response. Some connectors might not support it.
     * If there are multiple connectors attached to the gateway, only the first one
     * is actually called, the rest are usual <code>send()</code>.
     * 
     * @param message the message to be sent
     * @return the response promise
     */
    public Promise<Message, Exception> call(Message message);

    /**
     * Push a message to the gateway incoming sink, so it will be routed the
     * subscribers. The subscriber may or may not send back a response. If there is
     * no response the promise will never be completed.
     * 
     * @param message the message to be pushed
     * @return the promise
     */
    public Promise<Message, Exception> push(Message message);

    /**
     * Send a message and wait for response. The response will then be pushed into
     * the gateway incoming sink instead of returning to the caller.
     * 
     * Some connectors might not support it. If there are multiple connectors
     * attached to the gateway, only the first one is actually called, the rest are
     * usual <code>send()</code>.
     * 
     * @param message
     */
    public void callAndPush(Message message);

    /**
     * Sugar-syntactic method for <code>send()</code>. Create a message from body
     * and send it, ignoring status and response.
     * 
     * @param body the body to be sent
     * @see #send(Message)
     */
    public default void sendAny(Object body) {
        send(Message.ofAny(body));
    }

    /**
     * Sugar-syntactic method for <code>send()</code>. Create a message from headers
     * and body and send it, ignoring status and response.
     * 
     * @param headers the headers to be sent
     * @param body    the body to be sent
     * @see #send(Message)
     */
    public default void sendAny(BObject headers, Object body) {
        send(Message.ofAny(headers, body));
    }

    /**
     * Sugar-syntactic method for <code>sendWithAck()</code>. Create a message from
     * body and send it, wait for status but ignore the response.
     * 
     * @param body the body to be sent
     * @return the acknowlegement promise
     * @see #sendWithAck(Message)
     */
    public default Promise<Message, Exception> sendAnyWithAck(Object body) {
        return sendWithAck(Message.ofAny(body));
    }

    /**
     * Sugar-syntactic method for <code>sendWithAck()</code>. Create a message from
     * headers and body and send it, wait for status but ignore the response.
     * 
     * @param headers the headers to be sent
     * @param body    the body to be sent
     * @return the acknowledgement promise
     * @see #sendWithAck(Message)
     */
    public default Promise<Message, Exception> sendAnyWithAck(BObject headers, Object body) {
        return sendWithAck(Message.ofAny(headers, body));
    }

    /**
     * Sugar-syntactic method for <code>call()</code>. Create a message from body,
     * send it and wait for response.
     * 
     * @param body the body to be sent
     * @return the response promise
     * @see #call(Message)
     */
    public default Promise<Message, Exception> callAny(Object body) {
        return call(Message.ofAny(body));
    }

    /**
     * Sugar-syntactic method for <code>call()</code>. Create a message from headers
     * and body, send it and wait for response.
     * 
     * @param headers the headers to be sent
     * @param body    the body to be sent
     * @return the response promise
     * @see #call(Message)
     */
    public default Promise<Message, Exception> callAny(BObject headers, Object body) {
        return call(Message.ofAny(headers, body));
    }

    /**
     * Check if this gateway is auto-started.
     * 
     * @return true if and only if the gateway is configured to be auto-started
     */
    public boolean isAutoStart();
}
