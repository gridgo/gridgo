package io.gridgo.framework.support.generators.impl;

import java.util.Optional;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;

import io.gridgo.bean.BValue;
import io.gridgo.framework.support.generators.IdGenerator;

public class TimeBasedIdGenerator implements IdGenerator {

    private NoArgGenerator generator = Generators.timeBasedGenerator();

    @Override
    public Optional<BValue> generateId() {
        return Optional.of(BValue.of(generator.generate().toString()));
    }
}
