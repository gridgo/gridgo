package io.gridgo.connector;

import org.joo.promise4j.Promise;

import io.gridgo.framework.support.Message;

public interface Responder extends Producer {

    /**
     * Cannot make a call on a responder
     */
    @Override
    default Promise<Message, Exception> call(Message request) {
        throw new UnsupportedOperationException("Cannot make a call on a responder");
    }

    @Override
    default boolean isCallSupported() {
        return false;
    }
}
