package io.gridgo.framework.support.impl;

import io.gridgo.framework.support.Registry;

public class SystemEnvRegistry implements Registry {

    @Override
    public Object lookup(String name) {
        return System.getenv(name.replaceAll("\\.", "_"));
    }

    @Override
    public SystemEnvRegistry register(String name, Object answer) {
        throw new UnsupportedOperationException("System Env does not support register()");
    }
}
