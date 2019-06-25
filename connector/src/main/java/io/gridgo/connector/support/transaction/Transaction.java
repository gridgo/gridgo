package io.gridgo.connector.support.transaction;

import io.gridgo.connector.support.MessageProducer;
import io.gridgo.framework.support.Message;
import org.joo.promise4j.Promise;

/**
 * Represent an asychronous transaction. It can be used to call requests inside
 * a transaction boundary, commit the transaction or rollback it.
 */
public interface Transaction extends MessageProducer {

    /**
     * Commit the transaction and close the underlying connection. This method
     * should be idempotent, i.e calling multiple times has the same effect as
     * calling once.
     * 
     * @return the transaction itself
     */
    public Promise<Message, Exception> commit();

    /**
     * Rollback the transaction and close the underlying connection. This method
     * should be idempotent, i.e calling multiple times has the same effect as
     * calling once. Calling this method after {@link #commit()} is called has no
     * effect.
     * 
     * @return the transaction itself
     */
    public Promise<Message, Exception> rollback();
}
