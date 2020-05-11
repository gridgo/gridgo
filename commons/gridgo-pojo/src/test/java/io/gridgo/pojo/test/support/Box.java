package io.gridgo.pojo.test.support;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Box<T, K> {

    public <R> List<Map<R, Map<T, K>>> getData() {
        return null;
    }

    public <R> void setData(List<Map<R, Map<T, K>>> data) {

    }
}
