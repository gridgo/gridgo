package io.gridgo.connector.test;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.connector.support.ConnectionRef;

public class ConnectionRefUnitTest {

    @Test
    public void testMultiThread() throws InterruptedException {
        var ref = new ConnectionRef<Object>(new Object());
        var t1 = new Thread(() -> {
            for (int i = 0; i < 500; i++) {
                ref.ref();
            }
        });
        var t2 = new Thread(() -> {
            for (int i = 0; i < 500; i++) {
                ref.ref();
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        Assert.assertEquals(1000, ref.getRefCount());

        t1 = new Thread(() -> {
            for (int i = 0; i < 400; i++) {
                ref.deref();
            }
        });
        t2 = new Thread(() -> {
            for (int i = 0; i < 400; i++) {
                ref.deref();
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        Assert.assertEquals(200, ref.getRefCount());
    }

    @Test
    public void testMultiThreadExcess() throws InterruptedException {
        var ref = new ConnectionRef<Object>(new Object());
        var t1 = new Thread(() -> {
            for (int i = 0; i < 500; i++) {
                ref.ref();
            }
        });
        var t2 = new Thread(() -> {
            for (int i = 0; i < 500; i++) {
                ref.ref();
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        Assert.assertEquals(1000, ref.getRefCount());

        t1 = new Thread(() -> {
            for (int i = 0; i < 600; i++) {
                ref.deref();
            }
        });
        t2 = new Thread(() -> {
            for (int i = 0; i < 600; i++) {
                ref.deref();
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        Assert.assertEquals(0, ref.getRefCount());
    }

    @Test
    public void testSingleThread() {
        var ref = new ConnectionRef<Object>(new Object());
        Assert.assertNotNull(ref.getConnection());
        ref.ref();
        Assert.assertEquals(1, ref.getRefCount());
        ref.deref();
        Assert.assertEquals(0, ref.getRefCount());
        ref.ref();
        Assert.assertEquals(1, ref.getRefCount());
        ref.dispose();
        Assert.assertNull(ref.getConnection());
        Assert.assertEquals(1, ref.getRefCount());
    }
}
