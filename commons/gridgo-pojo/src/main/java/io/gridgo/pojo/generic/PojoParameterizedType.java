package io.gridgo.pojo.generic;

import java.util.List;

import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@Accessors(fluent = true)
@SuperBuilder
@ToString(callSuper = true)
public class PojoParameterizedType extends PojoType {

    @Singular
    private List<PojoType> actualTypeArguments;
}
