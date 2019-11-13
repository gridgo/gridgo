package io.gridgo.bean.test.support;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dslplatform.json.CompiledJson;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@CompiledJson
public class NumberCollectionPojo {

    private List<Byte> byteList;
    private List<Short> shortList;
    private List<Integer> integerList;
    private List<Long> longList;
    private List<Float> floatList;
    private List<Double> doubleList;

    private Set<Byte> byteSet;
    private Set<Short> shortSet;
    private Set<Integer> integerSet;
    private Set<Long> longSet;
    private Set<Float> floatSet;
    private Set<Double> doubleSet;

    private Map<String, Byte> byteMap;
    private Map<String, Short> shortMap;
    private Map<String, Integer> integerMap;
    private Map<String, Long> longMap;
    private Map<String, Float> floatMap;
    private Map<String, Double> doubleMap;

}
