package io.gridgo.utils.pojo.setter.perf;

import org.junit.Test;

import io.gridgo.utils.ObjectUtils;
import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.support.AbstractTest;
import io.gridgo.utils.pojo.support.PrimitiveVO;

public class TestPojoSetterPerf extends AbstractTest {

    private static final int TOTAL = 1000000;

    private final PrimitiveVO target = new PrimitiveVO();

    @Test
    public void testSimpleBoolean() throws Exception {
        String fieldName = "booleanValue";
        var value = true;

        PojoUtils.setValue(target, fieldName, value);
        ObjectUtils.setValue(target, fieldName, value);
        target.setBooleanValue(value);

        var started = System.nanoTime();
        for (int i = 0; i < TOTAL; i++) {
            PojoUtils.setValue(target, fieldName, value);
        }
        var elapsed = System.nanoTime() - started;
        print("PojoUtils", elapsed);

        started = System.nanoTime();
        for (int i = 0; i < TOTAL; i++) {
            ObjectUtils.setValue(target, fieldName, value);
        }
        elapsed = System.nanoTime() - started;
        print("ObjectUtils", elapsed);

        started = System.nanoTime();
        for (int i = 0; i < TOTAL; i++) {
            target.setBooleanValue(value);
        }
        elapsed = System.nanoTime() - started;
        print("Direct", elapsed);
    }

    protected void print(String type, long elapsed) {
        var throughput = (long) ((double) TOTAL / elapsed * 1e9);
        System.out.println("[" + type + "] Elapsed: " + elapsed / 1e6 + "ms. Throughput: " + throughput);
    }
    
}
