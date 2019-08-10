package io.gridgo.core.test;

import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.core.support.subscription.impl.Condition;
import io.gridgo.framework.support.Message;

public class MiscTest {

    @Test
    public void testCondition() {
        Assert.assertTrue(Condition.of(true).test(null));
        Assert.assertTrue(Condition.of(() -> true).test(null));
        Assert.assertTrue(((Predicate<Message>) msg -> true).test(null));
        Assert.assertTrue(Condition.of("1 + 1 == 2").test(null));
    }
}
