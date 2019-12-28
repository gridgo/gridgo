package io.gridgo.utils.pojo.support;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PojoWithCollection {

    private int intValue;

    private Object nullPointer;

    private List<String> list;

    private List<SimplePojo> pojoList;

    private List<String[]> nestedList;

    private Set<String> set;

    private Map<String, Object> map;

    private int[] intArr;

    private Object[] objArr;

    private SimplePojo pojo;

    private SimplePojo[] pojoArr;
}