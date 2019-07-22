package io.gridgo.utils.pojo.asm.setter;

import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.MethodVisitor;

import io.gridgo.utils.pojo.PojoMethodSignature;

public class PrimitiveSetterMethodBuilder extends AbstractSetterMethodBuilder {

    public static PrimitiveSetterMethodBuilder forType(Class<?> type) {
        if (type.isPrimitive()) {
            if (type == Boolean.TYPE) {
                return new PrimitiveSetterMethodBuilder(Boolean.class, "Z", "booleanValue");
            }
            if (type == Character.TYPE) {
                return new PrimitiveSetterMethodBuilder(Character.class, "C", "charValue");
            }
            if (type == Byte.TYPE) {
                return new PrimitiveSetterMethodBuilder(Byte.class, "B", "byteValue");
            }
            if (type == Short.TYPE) {
                return new PrimitiveSetterMethodBuilder(Short.class, "S", "shortValue");
            }
            if (type == Integer.TYPE) {
                return new PrimitiveSetterMethodBuilder(Integer.class, "I", "intValue");
            }
            if (type == Long.TYPE) {
                return new PrimitiveSetterMethodBuilder(Long.class, "J", "longValue");
            }
            if (type == Float.TYPE) {
                return new PrimitiveSetterMethodBuilder(Float.class, "F", "floatValue");
            }
            if (type == Double.TYPE) {
                return new PrimitiveSetterMethodBuilder(Double.class, "D", "doubleValue");
            }

            throw new IllegalArgumentException("Cannot findout primitive type signature for type: " + type);
        }
        return new PrimitiveSetterMethodBuilder();
    }

    private final String target;
    private final String signature;
    private final String methodTobeInvoked;

    private PrimitiveSetterMethodBuilder() {
        this.target = null;
        this.signature = null;
        this.methodTobeInvoked = null;
    }

    private PrimitiveSetterMethodBuilder(Class<?> wrapperClass, String primitiveDescriptor, String methodTobeInvoked) {
        this.target = wrapperClass.getName().replaceAll("\\.", "/");
        this.signature = "()" + primitiveDescriptor;
        this.methodTobeInvoked = methodTobeInvoked;
    }

    @Override
    protected String extractParamDescriptor(Class<?> type) {
        return this.target != null ? this.target : super.extractParamDescriptor(type);
    }

    @Override
    protected void onBeforeInvoke(Class<?> type, PojoMethodSignature methodSignature, MethodVisitor mv) {
        if (target != null) {
            mv.visitMethodInsn(INVOKEVIRTUAL, target, methodTobeInvoked, signature);
        }
    }
}
