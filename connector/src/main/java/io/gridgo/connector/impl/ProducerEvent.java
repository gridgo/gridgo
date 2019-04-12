package io.gridgo.connector.impl;

import org.joo.promise4j.Deferred;

import io.gridgo.framework.support.Message;
import lombok.Data;

@Data
final class ProducerEvent {

    private Message message;

    private Deferred<Message, Exception> deferred;

    void clear() {
        this.message = null;
        this.deferred = null;
    }

}
