package io.gridgo.pojo.test.support;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Box<T, K> {

    private List<Map<String, List<K[]>>> data;

    private List<Map<String, Primitive>> data1;

    private List<Primitive>[] data2;
}
