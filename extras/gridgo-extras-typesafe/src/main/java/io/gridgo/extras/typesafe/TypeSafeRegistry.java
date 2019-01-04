package io.gridgo.extras.typesafe;

import java.io.File;
import java.io.Reader;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;

import io.gridgo.framework.support.Registry;

public class TypeSafeRegistry implements Registry {

    private Config config;

    public TypeSafeRegistry() {
        this.config = ConfigFactory.load();
    }

    public TypeSafeRegistry(String resource) {
        this.config = ConfigFactory.load(resource);
    }

    public TypeSafeRegistry(Reader reader) {
        this.config = ConfigFactory.parseReader(reader);
    }

    public TypeSafeRegistry(File file) {
        this.config = ConfigFactory.parseFile(file);
    }

    public TypeSafeRegistry(Config config) {
        this.config = config;
    }

    @Override
    public Object lookup(String name) {
        try {
            return config.getAnyRef(name);
        } catch (ConfigException.Missing ex) {
            return null;
        }
    }

    @Override
    public Registry register(String name, Object answer) {
        throw new UnsupportedOperationException();
    }
}
