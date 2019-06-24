package io.gridgo.framework.support.impl;

import io.gridgo.framework.support.Registry;

public class XmlQueryRegistry implements Registry {
    
    private XmlRegistry registry;

    public XmlQueryRegistry(XmlRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Object lookup(String name) {
        return registry.lookup("/queries/query[@name='" + name + "']");
    }

    @Override
    public Registry register(String name, Object answer) {
        return this;
    }
}
