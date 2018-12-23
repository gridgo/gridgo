package io.gridgo.core.support;

import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.CompletableDeferredObject;

import io.gridgo.core.GridgoContext;
import io.gridgo.core.Processor;
import io.gridgo.core.support.impl.DefaultRoutingContext;
import io.gridgo.framework.support.Message;

/**
 * Wrapper of a <code>Processor</code>. This utility class can be used for
 * various purposes, including unit testing, using processors without any
 * gateway, etc.
 */
public class ProcessorWrapper {

    private GridgoContext gc;

    private Processor processor;

    public ProcessorWrapper(GridgoContext gc, Processor processor) {
        this.gc = gc;
        this.processor = processor;
    }

    /**
     * Process a message.
     * 
     * @param msg the message to be processed
     * @return the promise of the response
     */
    public Promise<Message, Exception> process(Message msg) {
        var deferred = new CompletableDeferredObject<Message, Exception>();
        var rc = new DefaultRoutingContext(null, msg, deferred);
        processor.process(rc, gc);
        return deferred.promise();
    }
}
