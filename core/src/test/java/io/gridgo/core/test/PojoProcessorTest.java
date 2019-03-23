package io.gridgo.core.test;

import java.util.List;

import org.joo.promise4j.Deferred;
import org.joo.promise4j.PromiseException;
import org.joo.promise4j.impl.CompletableDeferredObject;
import org.junit.Assert;
import org.junit.Test;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BObject;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.impl.AbstractPojoProcessor;
import io.gridgo.core.support.impl.DefaultRoutingContext;
import io.gridgo.framework.support.Message;

public class PojoProcessorTest {

    @Test
    public void testPrimitive() throws PromiseException, InterruptedException {
        var processor = new AbstractPojoProcessor<Integer>(Integer.class) {

            @Override
            public void processSingle(Integer request, Message msg, Deferred<Message, Exception> deferred, GridgoContext gc) {
                deferred.resolve(Message.ofAny(request));
            }
        };
        var deferred = new CompletableDeferredObject<Message, Exception>();
        var msg = Message.ofAny(1);
        var rc = new DefaultRoutingContext(null, msg, deferred);
        processor.process(rc, null);
        Assert.assertEquals(1, deferred.get().body().asValue().getInteger().intValue());
    }

    @Test
    public void testPojo() throws PromiseException, InterruptedException {
        var processor = new AbstractPojoProcessor<Pojo>(Pojo.class) {

            @Override
            public void processSingle(Pojo request, Message msg, Deferred<Message, Exception> deferred, GridgoContext gc) {
                deferred.resolve(Message.ofAny(request.getId()));
            }
        };
        var deferred = new CompletableDeferredObject<Message, Exception>();
        var msg = Message.ofAny(BObject.of("id", 1).setAny("name", "hello"));
        var rc = new DefaultRoutingContext(null, msg, deferred);
        processor.process(rc, null);
        Assert.assertEquals(1, deferred.get().body().asValue().getInteger().intValue());
    }

    @Test
    public void testList() throws PromiseException, InterruptedException {
        var processor = new AbstractPojoProcessor<Integer>(Integer.class) {

            @Override
            public void processMulti(List<Integer> request, Message msg, Deferred<Message, Exception> deferred, GridgoContext gc) {
                deferred.resolve(Message.ofAny(request.size()));
            }
        };
        var deferred = new CompletableDeferredObject<Message, Exception>();
        var msg = Message.ofAny(BArray.ofSequence(1, 2, 3));
        var rc = new DefaultRoutingContext(null, msg, deferred);
        processor.process(rc, null);
        Assert.assertEquals(3, deferred.get().body().asValue().getInteger().intValue());
    }

    @Test
    public void testListOfPojos() throws PromiseException, InterruptedException {
        var processor = new AbstractPojoProcessor<Pojo>(Pojo.class) {

            @Override
            public void processMulti(List<Pojo> request, Message msg, Deferred<Message, Exception> deferred, GridgoContext gc) {
                var response = request.stream() //
                                      .map(Pojo::getId) //
                                      .reduce((a, b) -> a + b) //
                                      .orElse(0);
                deferred.resolve(Message.ofAny(response));
            }
        };
        var deferred = new CompletableDeferredObject<Message, Exception>();
        var msg = Message.ofAny(BArray.ofSequence( //
                BObject.of("id", 1), //
                BObject.of("id", 2), //
                BObject.of("id", 3)) //
        );
        var rc = new DefaultRoutingContext(null, msg, deferred);
        processor.process(rc, null);
        Assert.assertEquals(6, deferred.get().body().asValue().getInteger().intValue());
    }
}
