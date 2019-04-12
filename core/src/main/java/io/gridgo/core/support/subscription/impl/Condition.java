package io.gridgo.core.support.subscription.impl;

import java.util.function.BooleanSupplier;

import org.joo.libra.Predicate;
import org.joo.libra.common.SimplePredicate;
import org.joo.libra.sql.SqlPredicate;

import io.gridgo.framework.support.Message;

public class Condition {

    private Condition() {
        // Nothing to do here
    }

    public static Predicate of(BooleanSupplier supplier) {
        return of(supplier.getAsBoolean());
    }

    public static Predicate of(boolean value) {
        return new SimplePredicate(value);
    }

    public static Predicate of(String condition) {
        return new SqlPredicate(condition);
    }

    public static Predicate of(Predicate predicate) {
        return predicate;
    }

    public static Predicate of(java.util.function.Predicate<Message> condition) {
        return new MessagePredicate(condition);
    }
}
