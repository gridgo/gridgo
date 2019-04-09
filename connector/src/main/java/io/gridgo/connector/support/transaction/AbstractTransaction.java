package io.gridgo.connector.support.transaction;

import io.gridgo.connector.impl.AbstractProducer;
import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.framework.support.Message;
import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.SimpleDonePromise;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractTransaction extends AbstractProducer implements Transaction {

    protected AbstractTransaction(ConnectorContext context){
        super(context);
    }

    private AtomicBoolean finished = new AtomicBoolean();

    @Override
    public Promise<Message, Exception> commit() {
        if (finished.compareAndSet(false, true)){
            return doCommit();
        }
        return new SimpleDonePromise<>(Message.ofEmpty());
    }

    @Override
    public Promise<Message, Exception> rollback() {
        if (finished.compareAndSet(false, true)){
            return doRollback();
        }
        return new SimpleDonePromise<>(Message.ofEmpty());
    }

    protected abstract Promise<Message, Exception> doCommit();

    protected abstract Promise<Message, Exception> doRollback();
}
