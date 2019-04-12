package io.gridgo.framework.support.generators.impl;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import io.gridgo.bean.BValue;
import io.gridgo.framework.support.generators.IdGenerator;

public class AtomicIdGenerator implements IdGenerator {

    private AtomicLong counter = new AtomicLong();

    @Override
    public Optional<BValue> generateId() {
        return Optional.of(BValue.of(counter.incrementAndGet()));
    }
}
