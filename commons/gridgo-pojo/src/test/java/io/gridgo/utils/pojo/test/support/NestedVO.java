package io.gridgo.utils.pojo.test.support;

import lombok.Data;

@Data
public class NestedVO {

    private PrimitiveVO primitiveVO;

    private PrimitiveVO2 primitiveVO2;

    private PrimitiveArrayVO arrayVO;

    private PrimitiveArrayVO2 arrayVO2;
}
