package io.gridgo.core.support.subscription.impl;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import io.gridgo.framework.support.Message;

public class Condition {

    private Condition() {
        // Nothing to do here
    }

    public static Predicate<Message> of(BooleanSupplier supplier) {
        return of(supplier.getAsBoolean());
    }

    public static Predicate<Message> of(boolean value) {
        return msg -> value;
    }

    public static Predicate<Message> of(String condition) {
        return new LibraSqlPredicate(condition);
    }
}
