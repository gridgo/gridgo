package io.gridgo.core.support.subscription.impl;

import org.joo.libra.Predicate;
import org.joo.libra.PredicateContext;
import org.joo.libra.support.exceptions.PredicateExecutionException;

import io.gridgo.framework.support.Message;

public class MessagePredicate implements Predicate {

    private java.util.function.Predicate<Message> condition;

    public MessagePredicate(java.util.function.Predicate<Message> condition) {
        this.condition = condition;
    }

    @Override
    public boolean satisfiedBy(PredicateContext context) throws PredicateExecutionException {
        return condition.test((Message) context.getContext());
    }
}
