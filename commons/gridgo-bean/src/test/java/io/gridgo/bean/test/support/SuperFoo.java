package io.gridgo.bean.test.support;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dslplatform.json.CompiledJson;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@CompiledJson
@SuperBuilder
@NoArgsConstructor
public class SuperFoo {

    private Map<String, Bar> barMap;

    private Map<String, long[]> longArrayMap;

    private List<int[]> intArrayList;

    private Set<Bar> barSet;
}
