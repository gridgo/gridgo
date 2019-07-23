package io.gridgo.utils.pojo;

import lombok.Getter;

public class DynamicClassLoader extends ClassLoader {

    @Getter
    private static final DynamicClassLoader instance = new DynamicClassLoader();

    private DynamicClassLoader() {
        super(Thread.currentThread().getContextClassLoader());
    }

    public Class<?> loadByteCode(String className, byte[] bytecode) {
        return super.defineClass(className, bytecode, 0, bytecode.length);
    }
}
