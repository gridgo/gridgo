package io.gridgo.utils.pojo.helper;

import org.junit.Assert;
import org.junit.Test;

public class MethodAccessorsTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNonStatic() throws NoSuchMethodException, SecurityException {
        var method = TestAccessors.class.getDeclaredMethod("nonStaticOneParam", Object.class);
        MethodAccessors.forStaticSingleParamFunction(method);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonPublic() throws NoSuchMethodException, SecurityException {
        var method = TestAccessors.class.getDeclaredMethod("nonPublicOneParam", Object.class);
        MethodAccessors.forStaticSingleParamFunction(method);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAbstract() throws NoSuchMethodException, SecurityException {
        var method = TestAccessors.class.getDeclaredMethod("nonPublicOneParam", Object.class);
        MethodAccessors.forStaticSingleParamFunction(method);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongParamCountSingle() throws NoSuchMethodException, SecurityException {
        var method = TestAccessors.class.getDeclaredMethod("publicOneParam", Object.class);
        MethodAccessors.forStaticTwoParamsFunction(method);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongParamCountTwo() throws NoSuchMethodException, SecurityException {
        var method = TestAccessors.class.getDeclaredMethod("publicTwoParams", Object.class, Object.class);
        MethodAccessors.forStaticSingleParamFunction(method);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testVoid() throws NoSuchMethodException, SecurityException {
        var method = TestAccessors.class.getDeclaredMethod("voidOneParam", Object.class);
        MethodAccessors.forStaticSingleParamFunction(method);
    }

    @Test
    public void testSingleParam() throws NoSuchMethodException, SecurityException {
        TestAccessors.obj = null;
        var method = TestAccessors.class.getDeclaredMethod("publicOneParam", Object.class);
        var accessor = MethodAccessors.forStaticSingleParamFunction(method);
        accessor.apply("test");
        Assert.assertEquals("test", TestAccessors.obj);
    }

    @Test
    public void testTwoParams() throws NoSuchMethodException, SecurityException {
        TestAccessors.obj = null;
        TestAccessors.obj2 = null;
        var method = TestAccessors.class.getDeclaredMethod("publicTwoParams", Object.class, Object.class);
        var accessor = MethodAccessors.forStaticTwoParamsFunction(method);
        accessor.apply("test2", "test3");
        Assert.assertEquals("test2", TestAccessors.obj);
        Assert.assertEquals("test3", TestAccessors.obj2);
    }
}
