package io.gridgo.utils.pojo;

import java.util.Map;

public interface ValueProvider {

    Object get(String fieldName);

    static ValueProvider forMap(Map<?, ?> map) {
        return map::get;
    }
}
