package io.gridgo.framework.support.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import io.gridgo.framework.support.Registry;

public class PropertiesFileRegistry implements Registry {

    private Properties props;

    public PropertiesFileRegistry(String fileName) {
        this.props = new Properties();
        try (var is = new FileInputStream(fileName)) {
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PropertiesFileRegistry(File file) {
        this.props = new Properties();
        try (var is = new FileInputStream(file)) {
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object lookup(String name) {
        return props.get(name);
    }

    @Override
    public PropertiesFileRegistry register(String name, Object answer) {
        props.put(name, answer);
        return this;
    }
}
