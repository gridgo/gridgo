package io.gridgo.utils.pojo.setter;

import io.gridgo.utils.pojo.PojoMethodSignature;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class PojoSetterSignature extends PojoMethodSignature {

    private final PojoSetter setter;
}
