package io.gridgo.utils.pojo.asm.setter;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.RETURN;

import org.objectweb.asm.MethodVisitor;

import io.gridgo.utils.pojo.PojoMethodSignature;

public class AbstractSetterMethodBuilder implements AsmSetterMethodBuilder {

    @Override
    public void buildMethod(Class<?> type, PojoMethodSignature signature, MethodVisitor mv) {

        String paramDescriptor = extractParamDescriptor(signature.getFieldType());
        String methodDescriptor = signature.getMethodDescriptor();
        String methodName = signature.getMethodName();
        String typeDescriptor = type.getName().replaceAll("\\.", "/");

        mv.visitCode();
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, typeDescriptor);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitTypeInsn(CHECKCAST, paramDescriptor);

        onBeforeInvoke(type, signature, mv);

        mv.visitMethodInsn(INVOKEVIRTUAL, typeDescriptor, methodName, methodDescriptor);
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 3);
        mv.visitEnd();
    }

    protected String extractParamDescriptor(Class<?> type) {
        return type.getName().replaceAll("\\.", "/");
    }

    protected void onBeforeInvoke(Class<?> type, PojoMethodSignature signature, MethodVisitor mv) {
        // do nothing
    }
}
