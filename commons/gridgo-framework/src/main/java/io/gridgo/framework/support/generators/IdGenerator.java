package io.gridgo.framework.support.generators;

import java.util.Optional;

import io.gridgo.bean.BValue;

public interface IdGenerator {

    public Optional<BValue> generateId();
}
