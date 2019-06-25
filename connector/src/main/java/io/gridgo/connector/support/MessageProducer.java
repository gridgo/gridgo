package io.gridgo.connector.support;

import org.joo.promise4j.Promise;

import io.gridgo.bean.BObject;
import io.gridgo.framework.support.Message;

/**
 * Represent a Message sender component.
 */
public interface MessageProducer {

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
     * Send a message and wait for acknowledgement but ignore the response. Use this
     * method if you must know the status of the sending.
     * 
     * @param message the message to be sent
     * @return the acknowledgement promise
     */
    public Promise<Message, Exception> sendWithAck(Message message);

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
     * Send a message and wait for response. Some connectors might not support it.
     * If there are multiple connectors attached to the gateway, only the first one
     * is actually called, the rest are usual <code>send()</code>.
     * 
     * @param message the message to be sent
     * @return the response promise
     */
    public Promise<Message, Exception> call(Message message);

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
}
