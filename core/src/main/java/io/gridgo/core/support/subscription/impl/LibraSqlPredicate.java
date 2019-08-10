package io.gridgo.core.support.subscription.impl;

import java.util.function.Predicate;

import org.joo.libra.PredicateContext;

import io.gridgo.framework.support.Message;

public class LibraSqlPredicate implements Predicate<Message> {
    
    private org.joo.libra.Predicate predicate;

    public LibraSqlPredicate(String condition) {
        this.predicate = new org.joo.libra.sql.SqlPredicate(condition);
    }

    @Override
    public boolean test(Message msg) {
        return predicate.satisfiedBy(new PredicateContext(msg));
    }
}
