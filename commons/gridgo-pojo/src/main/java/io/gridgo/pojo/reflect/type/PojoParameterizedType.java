package io.gridgo.pojo.reflect.type;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@Accessors(fluent = true)
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PojoParameterizedType extends PojoType {

    @Singular
    private List<PojoType> actualTypeArguments;
}
