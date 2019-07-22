package io.gridgo.utils.pojo.asm.getter;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.MethodVisitor;

import io.gridgo.utils.pojo.PojoMethodSignature;

public abstract class AbstractGetterMethodBuilder implements AsmGetterMethodBuilder {

    @Override
    public final void buildMethod(Class<?> type, PojoMethodSignature signature, MethodVisitor mv) {
        String typeDescriptor = type.getName().replaceAll("\\.", "/");

        String methodName = signature.getMethodName();
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, typeDescriptor);
        mv.visitMethodInsn(INVOKEVIRTUAL, typeDescriptor, methodName, signature.getMethodDescriptor());
        mv.visitInsn(onBeforeReturn(signature, mv));
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    protected int onBeforeReturn(PojoMethodSignature signature, MethodVisitor mv) {
        return ARETURN;
    }
}
