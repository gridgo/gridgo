package io.gridgo.utils.pojo.translator;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

class FunctionAccessorGenerator {

    static FunctionAccessor generate(Method method) throws Exception {
        var modifiers = method.getModifiers();
        var methodInfo = method.getName() + ", " + method.getDeclaringClass();
        if (!Modifier.isStatic(modifiers))
            throw new IllegalArgumentException("method must be static: " + methodInfo);

        if (!Modifier.isPublic(modifiers))
            throw new IllegalArgumentException("method must be public: " + methodInfo);

        if (Modifier.isAbstract(modifiers))
            throw new IllegalArgumentException("method cannot be abstract: " + methodInfo);

        var returnType = method.getReturnType();
        if (returnType == Void.class || returnType == void.class)
            throw new IllegalArgumentException("method must return non-void: " + methodInfo);

        if (method.getParameterCount() != 1)
            throw new IllegalArgumentException("method must accept 1 and only 1 parameter: " + methodInfo);

        return buildApplyMethod(method);
    }

    @SuppressWarnings("unchecked")
    private static FunctionAccessor buildApplyMethod(Method method) throws Exception {
        Class<?> target = method.getDeclaringClass();
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(target));

        String className = target.getName().replaceAll("\\.", "_") + "_" + method.getName() + "_" + System.nanoTime();
        CtClass cc = pool.makeClass(className);

        cc.defrost();
        cc.addInterface(pool.get(FunctionAccessor.class.getName()));

        String paramType = method.getParameterTypes()[0].getName();
        String body = "public Object apply(Object param) { \n"; //
        body += "\treturn " + target.getName() + "." + method.getName() + "((" + paramType + ") param);";
        body += "}"; // end of method

        cc.addMethod(CtMethod.make(body, cc));
        return (FunctionAccessor) cc.toClass().getConstructor().newInstance();
    }
}
