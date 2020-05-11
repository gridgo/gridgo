package io.gridgo.pojo.generic;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@Accessors(fluent = true)
@ToString
@SuperBuilder
public class PojoType {

    private @NonNull Class<?> rawType;
}
