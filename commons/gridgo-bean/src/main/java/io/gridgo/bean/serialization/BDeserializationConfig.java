package io.gridgo.bean.serialization;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BDeserializationConfig {

    private Class<?> targetType;
}
