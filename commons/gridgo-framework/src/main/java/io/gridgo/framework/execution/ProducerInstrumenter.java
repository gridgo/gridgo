package io.gridgo.framework.execution;

import java.util.function.Function;

import org.joo.promise4j.Promise;

import io.gridgo.framework.support.Message;

public interface ProducerInstrumenter {

    public Promise<Message, Exception> instrument(Message msg, Function<Message, Promise<Message, Exception>> supplier,
            String source);
}
