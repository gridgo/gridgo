package io.gridgo.utils.test;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.utils.wrapper.ByteArray;

public class ByteArrayUnitTest {

    @Test
    public void testSimple() {
        var byteArray1 = ByteArray.newInstance(new byte[] { 1, 2, 4, 8, 16, 32, 64 });
        Assert.assertEquals(-1603446134, byteArray1.hashCode());
        var byteArray2 = ByteArray.newInstanceWithJavaSafeHashCodeCalculator(new byte[] { 1, 2, 4, 8, 16, 32, 64 });
        Assert.assertEquals(1046614007, byteArray2.hashCode());
        var byteArray3 = ByteArray.newInstanceWithJavaUnsafeHashCodeCalculator(new byte[] { 1, 2, 4, 8, 16, 32, 64 });
        Assert.assertEquals(1046614007, byteArray3.hashCode());
        var byteArray4 = ByteArray.newInstanceWithJNIHashCodeCalculator(new byte[] { 1, 2, 4, 8, 16, 32, 64 });
        Assert.assertEquals(1046614007, byteArray4.hashCode());
        Assert.assertEquals(byteArray1, byteArray2);
        Assert.assertEquals(byteArray1, byteArray3);
        Assert.assertEquals(byteArray1, byteArray4);
    }
}
