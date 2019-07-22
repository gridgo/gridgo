package io.gridgo.utils.pojo.getter;

import io.gridgo.utils.pojo.PojoMethodSignature;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class PojoGetterSignature extends PojoMethodSignature {

    private final PojoGetter getter;
}
