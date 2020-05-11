package io.gridgo.pojo.test.support;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Box<T, K> {

    private List<Map<String, Map<T, K>>> data;
}
