package io.gridgo.connector.support.transaction;

import org.joo.promise4j.Promise;

public interface TransactionalComponent {

    public Promise<Transaction, Exception> createTransaction();
}
