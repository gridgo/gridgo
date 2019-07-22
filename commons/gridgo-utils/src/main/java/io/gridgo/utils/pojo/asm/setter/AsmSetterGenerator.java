package io.gridgo.utils.pojo.asm.setter;

import static io.gridgo.utils.PrimitiveUtils.isPrimitive;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_7;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.asm.DynamicClassLoader;
import io.gridgo.utils.pojo.setter.PojoSetter;
import io.gridgo.utils.pojo.setter.PojoSetterGenerator;

public class AsmSetterGenerator implements PojoSetterGenerator {

    private AsmSetterMethodBuilder getBuilderFor(Class<?> fieldType) {
        if (!(fieldType == String.class) && isPrimitive(fieldType)) {
            return PrimitiveSetterMethodBuilder.forType(fieldType);
        }
        return DefaultSetterMethodBuilder.getInstance();
    }

    @Override
    public PojoSetter generateSetter(Class<?> type, PojoMethodSignature signature) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        String className = type.getName().replaceAll("\\.", "_") + "_" + signature.getMethodName() + "_invoker";
        cw.visit(V1_7, // Java 1.7
                ACC_PUBLIC, // public class
                className, // package and name
                null, // signature (null means not generic)
                "java/lang/Object", // superclass
                new String[] { PojoSetter.class.getName().replaceAll("\\.", "/") }); // interfaces

        MethodVisitor con = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        con.visitVarInsn(ALOAD, 0);
        con.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
        con.visitInsn(RETURN);
        con.visitMaxs(1, 1);
        con.visitEnd();

        buildMethod(type, signature, cw);

        cw.visitEnd();

        Class<?> clz = DynamicClassLoader.getInstance().loadByteCode(className, cw.toByteArray());

        try {
            return (PojoSetter) clz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void buildMethod(Class<?> type, PojoMethodSignature signature, ClassWriter cw) {

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, // public method
                "set", // method name
                "(Ljava/lang/Object;Ljava/lang/Object;)V", // descriptor
                null, // signature (null means not generic)
                null); // exceptions (array of strings)

        this.getBuilderFor(signature.getFieldType()).buildMethod(type, signature, mv);
    }
}
