package io.gridgo.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;

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
		return 0;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] createArray(Class<T> clazz, int length) {
		return (T[]) Array.newInstance(clazz, length);
	}

	@SuppressWarnings("rawtypes")
	public static Object toPrimitiveTypeArray(Class<?> clazz, List list) {
		if (clazz.isPrimitive()) {
			if (clazz == Integer.TYPE) {
				int[] _arr = new int[list.size()];
				for (int i = 0; i < _arr.length; i++) {
					_arr[i] = (int) list.get(i);
				}
				return _arr;
			} else if (clazz == Long.TYPE) {
				long[] _arr = new long[list.size()];
				for (int i = 0; i < _arr.length; i++) {
					_arr[i] = (long) list.get(i);
				}
				return _arr;
			} else if (clazz == Double.TYPE) {
				double[] _arr = new double[list.size()];
				for (int i = 0; i < _arr.length; i++) {
					_arr[i] = (double) list.get(i);
				}
				return _arr;
			} else if (clazz == Float.TYPE) {
				float[] _arr = new float[list.size()];
				for (int i = 0; i < _arr.length; i++) {
					_arr[i] = (float) list.get(i);
				}
				return _arr;
			} else if (clazz == Byte.TYPE) {
				byte[] _arr = new byte[list.size()];
				for (int i = 0; i < _arr.length; i++) {
					_arr[i] = (byte) list.get(i);
				}
				return _arr;
			} else if (clazz == Short.TYPE) {
				short[] _arr = new short[list.size()];
				for (int i = 0; i < _arr.length; i++) {
					_arr[i] = (short) list.get(i);
				}
				return _arr;
			} else if (clazz == Character.TYPE) {
				char[] _arr = new char[list.size()];
				for (int i = 0; i < _arr.length; i++) {
					_arr[i] = (char) list.get(i);
				}
				return _arr;
			}
		}
		throw new IllegalArgumentException("first parameter, clazz, must be primitive type, got " + clazz);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T[] toArray(Class<T> clazz, List list) {
		T[] arr = (T[]) Array.newInstance(clazz, list.size());
		for (int i = 0; i < arr.length; i++) {
			arr[i] = (T) list.get(i);
		}
		return arr;
	}
}
