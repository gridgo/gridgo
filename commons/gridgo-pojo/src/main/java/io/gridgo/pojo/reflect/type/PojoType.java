package io.gridgo.pojo.reflect.type;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@Accessors(fluent = true)
@ToString
@SuperBuilder
@EqualsAndHashCode
public class PojoType {

    private @NonNull Class<?> rawType;
}
