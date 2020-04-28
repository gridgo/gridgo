package io.gridgo.pojo.support;

import java.lang.reflect.InvocationTargetException;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;

import io.gridgo.utils.pojo.exception.PojoException;

public class MethodAccessorCompiler {

    @SuppressWarnings("unchecked")
    protected final <T> T doCompile(String className, String classBody) {
        try {
            var compiler = new SimpleCompiler();
            compiler.cook(classBody);
            return (T) compiler.getClassLoader().loadClass(className).getConstructor().newInstance();
        } catch (CompileException //
                | InstantiationException //
                | IllegalAccessException //
                | IllegalArgumentException //
                | InvocationTargetException //
                | NoSuchMethodException //
                | SecurityException //
                | ClassNotFoundException e) {
            throw new PojoException("Cannot create otac class: " + className, e);
        }
    }
}
