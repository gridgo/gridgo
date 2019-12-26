package io.gridgo.utils.pojo.setter.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SimplePrimitiveData extends SimpleGenericData implements PrimitiveData {

    @Getter
    private final Object data;
}
