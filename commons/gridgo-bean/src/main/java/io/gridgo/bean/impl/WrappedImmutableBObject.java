package io.gridgo.bean.impl;

import java.util.Map;

import io.gridgo.bean.ImmutableBObject;
import io.gridgo.bean.WrappedBObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@SuppressWarnings("unchecked")
public class WrappedImmutableBObject extends AbstractBObject implements ImmutableBObject, WrappedBObject {

    @Getter
    private final Map<?, ?> source;
}
