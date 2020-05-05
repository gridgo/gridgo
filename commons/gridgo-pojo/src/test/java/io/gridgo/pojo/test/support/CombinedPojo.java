package io.gridgo.pojo.test.support;

import lombok.Data;

@Data
public class CombinedPojo {

    private Primitive primitiveVo;

    private Wrapper wrapperVo;

    private WrapperArray wrapperArrayVo;
}
