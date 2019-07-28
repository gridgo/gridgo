package io.gridgo.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;

import io.gridgo.utils.exception.UnsupportedTypeException;
import lombok.NonNull;

public final class ArrayUtils {

    @FunctionalInterface
    public static interface ForeachCallback<T> {
        void apply(T element);
    }

    @FunctionalInterface
    public static interface ForeachCallback2<T> {
        void apply(T element, int index, boolean isEnd);
    }

    public static boolean isArrayOrCollection(Class<?> clazz) {
        if (clazz != null) {
            return clazz.isArray() || Collection.class.isAssignableFrom(clazz);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T> void foreach(Object arrayOrCollection, ForeachCallback<T> callback) {
        foreach(arrayOrCollection, (ele, index, isEnd) -> callback.apply((T) ele));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void foreachArrayPrimitive(@NonNull Object obj, @NonNull ForeachCallback callback) {
        foreachArrayPrimitive(obj, (ele, index, end) -> callback.apply(ele));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void foreachArrayPrimitive(@NonNull Object obj, @NonNull ForeachCallback2 callback) {
        Class<? extends Object> type = obj.getClass();

        if (type == boolean[].class) {
            boolean[] arr = (boolean[]) obj;
            int length = arr.length;
            int end = length - 1;
            for (int i = 0; i < length; i++) {
                callback.apply(arr[i], i, i == end);
            }
            return;
        }

        if (type == char[].class) {
            char[] arr = (char[]) obj;
            int length = arr.length;
            int end = length - 1;
            for (int i = 0; i < length; i++) {
                callback.apply(arr[i], i, i == end);
            }
            return;
        }

        if (type == byte[].class) {
            byte[] arr = (byte[]) obj;
            int length = arr.length;
            int end = length - 1;
            for (int i = 0; i < length; i++) {
                callback.apply(arr[i], i, i == end);
            }
            return;
        }

        if (type == short[].class) {
            short[] arr = (short[]) obj;
            int length = arr.length;
            int end = length - 1;
            for (int i = 0; i < length; i++) {
                callback.apply(arr[i], i, i == end);
            }
            return;
        }

        if (type == int[].class) {
            int[] arr = (int[]) obj;
            int length = arr.length;
            int end = length - 1;
            for (int i = 0; i < length; i++) {
                callback.apply(arr[i], i, i == end);
            }
            return;
        }

        if (type == long[].class) {
            long[] arr = (long[]) obj;
            int length = arr.length;
            int end = length - 1;
            for (int i = 0; i < length; i++) {
                callback.apply(arr[i], i, i == end);
            }
            return;
        }

        if (type == float[].class) {
            float[] arr = (float[]) obj;
            int length = arr.length;
            int end = length - 1;
            for (int i = 0; i < length; i++) {
                callback.apply(arr[i], i, i == end);
            }
            return;
        }

        if (type == double[].class) {
            double[] arr = (double[]) obj;
            int length = arr.length;
            int end = length - 1;
            for (int i = 0; i < length; i++) {
                callback.apply(arr[i], i, i == end);
            }
            return;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void foreachArray(@NonNull Object obj, @NonNull ForeachCallback2<T> callback) {
        Class<T> componentType = (Class<T>) obj.getClass().getComponentType();
        if (componentType.isPrimitive()) {
            foreachArrayPrimitive(obj, callback);
            return;
        }

        T[] arr = (T[]) obj;
        int length = arr.length;
        int end = length - 1;
        for (int i = 0; i < length; i++) {
            callback.apply(arr[i], i, i == end);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void foreachArray(@NonNull Object obj, @NonNull ForeachCallback<T> callback) {
        Class<T> componentType = (Class<T>) obj.getClass().getComponentType();
        if (componentType.isPrimitive()) {
            foreachArrayPrimitive(obj, callback);
            return;
        }

        T[] arr = (T[]) obj;
        int length = arr.length;
        for (int i = 0; i < length; i++) {
            callback.apply(arr[i]);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void foreach(Object arrayOrCollection, ForeachCallback2<T> callback) {
        if (arrayOrCollection != null && callback != null) {
            if (arrayOrCollection.getClass().isArray()) {
                foreachArray(arrayOrCollection, callback);
            } else if (arrayOrCollection instanceof Collection) {
                int length = ((Collection<?>) arrayOrCollection).size();
                int i = 0;
                for (Object element : (Collection<?>) arrayOrCollection) {
                    callback.apply((T) element, i++, i == length - 1);
                }
            } else {
                throw new IllegalArgumentException(
                        "cannot perform foreach for unsupported type: " + arrayOrCollection.getClass().getName());
            }
        }
    }

    public static int length(Object arrayCollection) {
        if (arrayCollection != null) {
            if (arrayCollection.getClass().isArray()) {
                return Array.getLength(arrayCollection);
            } else if (arrayCollection instanceof Collection) {
                return ((Collection<?>) arrayCollection).size();
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] createArray(Class<T> clazz, int length) {
        return (T[]) Array.newInstance(clazz, length);
    }

    @SuppressWarnings("unchecked")
    public static <T> T createPrimitiveArray(Class<?> type, int length) {
        if (type.isPrimitive()) {
            if (type == Boolean.TYPE) {
                return (T) new boolean[length];
            }
            if (type == Character.TYPE) {
                return (T) new char[length];
            }
            if (type == Byte.TYPE) {
                return (T) new byte[length];
            }
            if (type == Short.TYPE) {
                return (T) new short[length];
            }
            if (type == Integer.TYPE) {
                return (T) new int[length];
            }
            if (type == Long.TYPE) {
                return (T) new long[length];
            }
            if (type == Float.TYPE) {
                return (T) new float[length];
            }
            if (type == Double.TYPE) {
                return (T) new double[length];
            }
            throw new UnsupportedTypeException("cannot create primitive type for: " + type);
        }
        throw new IllegalArgumentException("Expected primitive type, got: " + type);
    }

    public static Object createPrimitiveArray(Class<?> type, List<?> list) {
        if (type.isPrimitive()) {
            int length = list.size();
            if (type == Boolean.TYPE) {
                var arr = new boolean[length];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = PrimitiveUtils.getBooleanValueFrom(list.get(i));
                }
                return arr;
            }
            if (type == Character.TYPE) {
                var arr = new char[length];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = PrimitiveUtils.getCharValueFrom(list.get(i));
                }
                return arr;
            }
            if (type == Byte.TYPE) {
                var arr = new byte[length];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = PrimitiveUtils.getByteValueFrom(list.get(i));
                }
                return arr;
            }
            if (type == Short.TYPE) {
                var arr = new short[length];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = PrimitiveUtils.getShortValueFrom(list.get(i));
                }
                return arr;
            }
            if (type == Integer.TYPE) {
                var arr = new int[length];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = PrimitiveUtils.getIntegerValueFrom(list.get(i));
                }
                return arr;
            }
            if (type == Long.TYPE) {
                var arr = new long[length];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = PrimitiveUtils.getLongValueFrom(list.get(i));
                }
                return arr;
            }
            if (type == Float.TYPE) {
                var arr = new float[length];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = PrimitiveUtils.getFloatValueFrom(list.get(i));
                }
                return arr;
            }
            if (type == Double.TYPE) {
                var arr = new double[length];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = PrimitiveUtils.getDoubleValueFrom(list.get(i));
                }
                return arr;
            }
            throw new UnsupportedTypeException("cannot create primitive type for: " + type);
        }
        throw new IllegalArgumentException("Expected primitive type, got: " + type);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(Class<T> clazz, List<?> list) {
        T[] result = createArray(clazz, list.size());
        for (int i = 0; i < result.length; i++) {
            result[i] = (T) list.get(i);
        }
        return (T[]) result;
    }

    @SuppressWarnings("rawtypes")
    public static Object toPrimitiveTypeArray(Class<?> clazz, List list) {
        if (!clazz.isPrimitive())
            throw new IllegalArgumentException("expected primitive type, got " + clazz);

        if (clazz == Integer.TYPE) {
            int[] arr = new int[list.size()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = (int) list.get(i);
            }
            return arr;
        }
        if (clazz == Long.TYPE) {
            long[] arr = new long[list.size()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = (long) list.get(i);
            }
            return arr;
        }
        if (clazz == Double.TYPE) {
            double[] arr = new double[list.size()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = (double) list.get(i);
            }
            return arr;
        }
        if (clazz == Float.TYPE) {
            float[] arr = new float[list.size()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = (float) list.get(i);
            }
            return arr;
        }
        if (clazz == Byte.TYPE) {
            byte[] arr = new byte[list.size()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = (byte) list.get(i);
            }
            return arr;
        }
        if (clazz == Short.TYPE) {
            short[] arr = new short[list.size()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = (short) list.get(i);
            }
            return arr;
        }
        if (clazz == Character.TYPE) {
            char[] arr = new char[list.size()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = (char) list.get(i);
            }
            return arr;
        }
        throw new UnsupportedTypeException("Unsupported type: " + clazz.getName());
    }

    @SuppressWarnings("rawtypes")
    public static Object entryAt(@NonNull Object arrayOrList, int index) {
        if (index < 0)
            throw new IllegalArgumentException("Index must >= 0, got: " + index);
        if (arrayOrList instanceof List)
            return ((List) arrayOrList).get(index);
        if (arrayOrList.getClass().isArray())
            return Array.get(arrayOrList, index);
        throw new IllegalArgumentException(
                "First argument expected an array or a list, got: " + arrayOrList.getClass());
    }

    public static String toString(Object value) {
        if (value != null) {
            StringBuilder sb = new StringBuilder();
            foreachArray(value, (ele, index, end) -> {
                sb.append(value);
                if (!end) {
                    sb.append(", ");
                }
            });
            return sb.toString();
        }
        return null;
    }
}
