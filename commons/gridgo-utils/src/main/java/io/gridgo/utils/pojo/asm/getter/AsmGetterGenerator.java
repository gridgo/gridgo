package io.gridgo.utils.pojo.asm.getter;

import static io.gridgo.utils.PrimitiveUtils.isPrimitive;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_7;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import io.gridgo.utils.pojo.DynamicClassLoader;
import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.asm.AsmMethodBuilder;
import io.gridgo.utils.pojo.getter.PojoGetter;
import io.gridgo.utils.pojo.getter.PojoGetterGenerator;

public class AsmGetterGenerator implements PojoGetterGenerator {

    private AsmMethodBuilder getBuilderFor(Class<?> fieldType) {
        if (!(fieldType == String.class) && isPrimitive(fieldType)) {
            return PrimitiveGetterMethodBuilder.forType(fieldType);
        }
        return DefaultGetterMethodBuilder.getInstance();
    }

    @Override
    public PojoGetter generateGetter(Class<?> type, PojoMethodSignature signature) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        String className = type.getName().replaceAll("\\.", "_") + "_" + signature.getMethodName() + "_invoker";
        cw.visit(V1_7, // Java 1.7
                ACC_PUBLIC, // public class
                className, // package and name
                null, // signature (null means not generic)
                "java/lang/Object", // superclass
                new String[] { PojoGetter.class.getName().replaceAll("\\.", "/") }); // interfaces

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        buildMethod(type, signature, cw);

        cw.visitEnd();

        Class<?> clz = DynamicClassLoader.getInstance().define(className, cw.toByteArray());

        try {
            return (PojoGetter) clz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void buildMethod(Class<?> type, PojoMethodSignature signature, ClassWriter cw) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, // public method
                "get", // method name
                "(Ljava/lang/Object;)Ljava/lang/Object;", // descriptor
                null, // signature (null means not generic)
                null); // exceptions (array of strings)

        this.getBuilderFor(signature.getFieldType()).buildMethod(type, signature, mv);
    }
}
