package io.gridgo.utils.pojo.setter.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SimpleReferenceData extends SimpleGenericData implements ReferenceData {

    @Getter
    private final Object reference;
}
