package io.gridgo.framework.support.impl;

import io.gridgo.framework.support.Registry;

public class SystemPropertyRegistry implements Registry {

    @Override
    public Object lookup(String name) {
        return System.getProperty(name);
    }

    @Override
    public SystemPropertyRegistry register(String name, Object answer) {
        System.setProperty(name, answer != null ? answer.toString() : null);
        return this;
    }
}
