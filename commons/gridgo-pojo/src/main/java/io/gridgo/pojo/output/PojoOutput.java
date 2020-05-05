package io.gridgo.pojo.output;

public interface PojoOutput {

    public static boolean isDirectSupported(Class<?> fieldType) {
        return fieldType.isPrimitive() || fieldType == String.class || fieldType == String[].class //
                || (fieldType.isArray() && fieldType.getComponentType().isPrimitive());
    }
}
