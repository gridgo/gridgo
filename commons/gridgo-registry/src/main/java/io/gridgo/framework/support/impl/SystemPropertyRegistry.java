package io.gridgo.framework.support.impl;

import io.gridgo.framework.support.Registry;
import lombok.NonNull;

public class SystemPropertyRegistry implements Registry {

    @Override
    public Object lookup(String name) {
        return System.getProperty(name);
    }

    @Override
    public SystemPropertyRegistry register(String name, @NonNull Object answer) {
        System.setProperty(name, answer.toString());
        return this;
    }
}
