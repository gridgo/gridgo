package io.gridgo.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;

import io.gridgo.utils.exception.UnsupportedTypeException;
import lombok.NonNull;

public final class ArrayUtils {

    public static interface ForeachCallback<T> {
        void apply(T element);
    }

    public static boolean isArrayOrCollection(Class<?> clazz) {
        if (clazz != null) {
            if (clazz.isArray()) {
                return true;
            }
            if (Collection.class.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T> void foreach(Object arrayOrCollection, ForeachCallback<T> callback) {
        if (arrayOrCollection != null && callback != null) {
            if (arrayOrCollection.getClass().isArray()) {
                for (int i = 0; i < Array.getLength(arrayOrCollection); i++) {
                    callback.apply((T) Array.get(arrayOrCollection, i));
                }
            } else if (arrayOrCollection instanceof Collection) {
                for (Object element : (Collection<?>) arrayOrCollection) {
                    callback.apply((T) element);
                }
            } else {
                throw new IllegalArgumentException("cannot perform foreach for unsupported type: " + arrayOrCollection.getClass().getName());
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

    @SuppressWarnings("rawtypes")
    public static Object toPrimitiveTypeArray(Class<?> clazz, List list) {
        if (!clazz.isPrimitive())
            throw new IllegalArgumentException("first parameter, clazz, must be primitive type, got " + clazz);
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> T[] toArray(Class<T> clazz, List list) {
        T[] arr = (T[]) Array.newInstance(clazz, list.size());
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (T) list.get(i);
        }
        return arr;
    }

    @SuppressWarnings("rawtypes")
    public static Object entryAt(@NonNull Object arrayOrList, int index) {
        if (index < 0)
            throw new IllegalArgumentException("Index must >= 0, got: " + index);
        if (arrayOrList instanceof List)
            return ((List) arrayOrList).get(index);
        if (arrayOrList.getClass().isArray())
            return Array.get(arrayOrList, index);
        throw new IllegalArgumentException("First argument expected an array or a list, got: " + arrayOrList.getClass());
    }
}
