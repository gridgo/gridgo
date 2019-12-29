package io.gridgo.utils.pojo.helper;

public abstract class TestAccessors {

    static Object obj = null;
    static Object obj2 = null;

    public int nonStaticOneParam(Object obj) {
        nonPublicOneParam(obj);
        return 0;
    }

    private static int nonPublicOneParam(Object obj) {
        return 0;
    }

    public static int publicOneParam(Object obj) {
        TestAccessors.obj = obj;
        return 0;
    }

    public static void voidOneParam(Object obj) {
        TestAccessors.obj = obj;
    }

    public static int publicTwoParams(Object obj, Object obj2) {
        TestAccessors.obj = obj;
        TestAccessors.obj2 = obj2;
        return 0;
    }
}