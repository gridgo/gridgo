package io.gridgo.utils.pojo;

import lombok.Getter;

public class DynamicClassLoader extends ClassLoader {

    @Getter
    private static final DynamicClassLoader instance = new DynamicClassLoader();

    private DynamicClassLoader() {
        super(DynamicClassLoader.class.getClassLoader());
    }

    public Class<?> define(String className, byte[] bytecode) {
        return super.defineClass(className, bytecode, 0, bytecode.length);
    }
}
