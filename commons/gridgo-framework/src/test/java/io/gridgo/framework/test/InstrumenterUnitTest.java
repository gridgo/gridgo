package io.gridgo.framework.test;

import org.joo.promise4j.Deferred;
import org.joo.promise4j.Promise;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.Function;

import io.gridgo.framework.execution.ExecutionStrategyInstrumenter;
import io.gridgo.framework.execution.ProducerInstrumenter;
import io.gridgo.framework.execution.impl.WrappedExecutionStrategyInstrumenter;
import io.gridgo.framework.execution.impl.WrappedProducerInstrumenter;
import io.gridgo.framework.support.Message;
import lombok.Getter;

public class InstrumenterUnitTest {

    @Test
    public void testWrappedTwoProducerInstrumenters() {
        var cpi1 = new CounterInstrumenter();
        var cpi2 = new CounterInstrumenter();
        var wrappedInstrumenter = new WrappedProducerInstrumenter(cpi1, cpi2);
        Assert.assertEquals(0, cpi1.count);
        Assert.assertEquals(0, cpi2.count);
        wrappedInstrumenter.instrument(Message.ofEmpty(), Promise::of, null);
        Assert.assertEquals(1, cpi1.count);
        Assert.assertEquals(1, cpi2.count);
    }

    @Test
    public void testWrappedTwoExecutionStrategyInstrumenters() {
        var cpi1 = new CounterInstrumenter();
        var cpi2 = new CounterInstrumenter();
        var wrappedInstrumenter = new WrappedExecutionStrategyInstrumenter(cpi1, cpi2);
        Assert.assertEquals(0, cpi1.count);
        Assert.assertEquals(0, cpi2.count);
        wrappedInstrumenter.instrument(Message.ofEmpty(), null, () -> {});
        Assert.assertEquals(1, cpi1.count);
        Assert.assertEquals(1, cpi2.count);
    }

    class CounterInstrumenter implements ProducerInstrumenter, ExecutionStrategyInstrumenter {

        @Getter
        private long count = 0;

        @Override
        public Promise<Message, Exception> instrument(Message msg,
                Function<Message, Promise<Message, Exception>> supplier, String source) {
            count++;
            return supplier.apply(msg);
        }

        @Override
        public Runnable instrument(Message msg, Deferred<Message, Exception> deferred, Runnable runnable) {
            count++;
            return runnable;
        }
    }
}
