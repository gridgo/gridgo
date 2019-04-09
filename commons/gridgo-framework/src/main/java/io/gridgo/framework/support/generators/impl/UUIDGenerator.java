package io.gridgo.framework.support.generators.impl;

import java.util.Optional;
import java.util.UUID;

import io.gridgo.bean.BValue;
import io.gridgo.framework.support.generators.IdGenerator;

public class UUIDGenerator implements IdGenerator {

    @Override
    public Optional<BValue> generateId() {
        return Optional.of(BValue.of(UUID.randomUUID().toString()));
    }
}
