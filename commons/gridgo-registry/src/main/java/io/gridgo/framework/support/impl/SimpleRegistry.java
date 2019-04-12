package io.gridgo.framework.support.impl;

import java.util.HashMap;
import java.util.Map;

import io.gridgo.framework.support.Registry;

public class SimpleRegistry implements Registry {

    private Map<String, Object> map = new HashMap<>();

    @Override
    public Object lookup(String name) {
        return map.get(name);
    }

    @Override
    public SimpleRegistry register(String name, Object answer) {
        map.put(name, answer);
        return this;
    }
}
