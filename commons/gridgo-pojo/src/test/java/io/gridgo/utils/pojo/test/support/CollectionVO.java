package io.gridgo.utils.pojo.test.support;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Data;

@Data
public class CollectionVO {

    private List<PrimitiveVO> listPrimitive;

    private Set<PrimitiveVO> setPrimitive;

    private Map<String, PrimitiveVO> mapPrimitive;
}
