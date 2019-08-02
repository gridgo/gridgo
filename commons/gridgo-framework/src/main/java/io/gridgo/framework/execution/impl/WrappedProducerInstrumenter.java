package io.gridgo.framework.execution.impl;

import java.util.function.Function;

import org.joo.promise4j.Promise;

import io.gridgo.framework.execution.ProducerInstrumenter;
import io.gridgo.framework.support.Message;

public class WrappedProducerInstrumenter implements ProducerInstrumenter {

    private ProducerInstrumenter[] instrumenters;

    public WrappedProducerInstrumenter(ProducerInstrumenter... instrumenters) {
        this.instrumenters = instrumenters;
    }

    @Override
    public Promise<Message, Exception> instrument(Message msg, Function<Message, Promise<Message, Exception>> supplier,
            String source) {
        var instrumented = supplier;
        for (var instrumenter : instrumenters) {
            instrumented = instrument(instrumenter, instrumented, source);
        }
        return instrumented.apply(msg);
    }

    private Function<Message, Promise<Message, Exception>> instrument(ProducerInstrumenter instrumenter,
            Function<Message, Promise<Message, Exception>> instrumented, String source) {
        return message -> instrumenter.instrument(message, instrumented, source);
    }
}
