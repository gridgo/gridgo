package io.gridgo.utils.pojo.asm.getter;

import static javassist.bytecode.Opcode.ARETURN;
import static javassist.bytecode.Opcode.INVOKESTATIC;

import org.objectweb.asm.MethodVisitor;

import io.gridgo.utils.pojo.PojoMethodSignature;

public class PrimitiveGetterMethodBuilder extends AbstractGetterMethodBuilder {

    public static PrimitiveGetterMethodBuilder forType(Class<?> type) {
        if (type.isPrimitive()) {
            if (type == Boolean.TYPE) {
                return new PrimitiveGetterMethodBuilder(Boolean.class, "Z");
            }
            if (type == Character.TYPE) {
                return new PrimitiveGetterMethodBuilder(Character.class, "C");
            }
            if (type == Byte.TYPE) {
                return new PrimitiveGetterMethodBuilder(Byte.class, "B");
            }
            if (type == Short.TYPE) {
                return new PrimitiveGetterMethodBuilder(Short.class, "S");
            }
            if (type == Integer.TYPE) {
                return new PrimitiveGetterMethodBuilder(Integer.class, "I");
            }
            if (type == Long.TYPE) {
                return new PrimitiveGetterMethodBuilder(Long.class, "J");
            }
            if (type == Float.TYPE) {
                return new PrimitiveGetterMethodBuilder(Float.class, "F");
            }
            if (type == Double.TYPE) {
                return new PrimitiveGetterMethodBuilder(Double.class, "D");
            }

            throw new IllegalArgumentException("Cannot findout primitive type signature for type: " + type);
        }
        return new PrimitiveGetterMethodBuilder((String) null, null);
    }

    private final String target;
    private final String signature;

    private PrimitiveGetterMethodBuilder(String target, String signature) {
        this.target = target;
        this.signature = signature;
    }

    private PrimitiveGetterMethodBuilder(Class<?> wrapperClass, String primitiveDescriptor) {
        this.target = wrapperClass.getName().replaceAll("\\.", "/");
        this.signature = "(" + primitiveDescriptor + ")L" + this.target + ";";
    }

    @Override
    protected int onBeforeReturn(PojoMethodSignature methodSignature, MethodVisitor mv) {
        if (this.target != null) {
            mv.visitMethodInsn(INVOKESTATIC, target, "valueOf", signature);
        }
        return ARETURN;
    }
}
