package io.gridgo.framework.support.generators.impl;

import java.util.Optional;

import io.gridgo.bean.BValue;
import io.gridgo.framework.support.generators.IdGenerator;

public class NoOpIdGenerator implements IdGenerator {

    @Override
    public Optional<BValue> generateId() {
        return Optional.empty();
    }
}
