package io.gridgo.utils.pojo.helper;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import io.gridgo.utils.PrimitiveUtils;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class MethodAccessors {

    private static void checkValidMethod(Method method, int requireParamCount) {
        var modifiers = method.getModifiers();
        var methodInfo = method.getName() + ", " + method.getDeclaringClass();
        if (!Modifier.isStatic(modifiers))
            throw new IllegalArgumentException("method must be static: " + methodInfo);

        if (!Modifier.isPublic(modifiers))
            throw new IllegalArgumentException("method must be public: " + methodInfo);

        var paramCount = method.getParameterCount();
        if (paramCount != requireParamCount)
            throw new IllegalArgumentException(
                    "method is required for " + requireParamCount + " params, got " + paramCount + ": " + methodInfo);

        var returnType = method.getReturnType();
        if (returnType == void.class)
            throw new IllegalArgumentException("method is required to return value, got void: " + methodInfo);
    }

    @SuppressWarnings("unchecked")
    private static <T> T buildStaticMethodAccessor(Method method, Class<T> theInterface, String interfaceFunctionName,
            String interfaceReturnTypeName) {
        try {
            Class<?> target = method.getDeclaringClass();
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(target));

            String className = target.getName().replaceAll("\\.", "_") + "_" + method.getName() + "_"
                    + System.nanoTime();
            CtClass cc = pool.makeClass(className);

            cc.defrost();
            cc.addInterface(pool.get(theInterface.getName()));

            var paramCount = 0;
            var paramKeys = new StringBuilder();
            var paramValues = new StringBuilder();
            for (var paramType : method.getParameterTypes()) {
                if (paramCount > 0) {
                    paramValues.append(", ");
                    paramKeys.append(", ");
                }
                var paramName = "param" + paramCount++;
                paramKeys.append("Object " + paramName);
                paramValues.append("(" + paramType.getName() + ") " + paramName);
            }

            var returnValue = target.getName() + "." + method.getName() + "(" + paramValues.toString() + ")";
            var returnType = method.getReturnType();
            if (returnType.isPrimitive() && !returnType.isArray()) {
                var wrappedForReturnType = PrimitiveUtils.getWrapperType(returnType);
                returnValue = wrappedForReturnType.getName() + ".valueOf(" + returnValue + ")";
            }
            var methodCall = ("void".equals(interfaceReturnTypeName) ? "" : "return ") + returnValue;

            String body = "public " + interfaceReturnTypeName + " " + interfaceFunctionName //
                    + "(" + paramKeys.toString() + ") { " + methodCall + ";}"; // end of method

            cc.addMethod(CtMethod.make(body, cc));
            return (T) cc.toClass().getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static FunctionAccessor forStaticSingleParamFunction(Method method) {
        checkValidMethod(method, 1);
        return buildStaticMethodAccessor(method, FunctionAccessor.class, "apply", "Object");
    }

    public static BiFunctionAccessor forStaticTwoParamsFunction(Method method) {
        checkValidMethod(method, 2);
        return buildStaticMethodAccessor(method, BiFunctionAccessor.class, "apply", "Object");
    }
}
