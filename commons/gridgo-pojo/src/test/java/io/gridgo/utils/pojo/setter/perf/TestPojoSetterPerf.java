package io.gridgo.utils.pojo.setter.perf;

import java.text.DecimalFormat;

import org.junit.Test;

import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.support.AbstractTest;
import io.gridgo.utils.pojo.support.PrimitiveVO;

public class TestPojoSetterPerf extends AbstractTest {

    private static final int TOTAL = 1000000;

    private final PrimitiveVO target = new PrimitiveVO();

    @Test
    public void testSimpleBoolean() throws Exception {
        System.out.println("Test pojo setter performance: ");
        String fieldName = "booleanValue";
        var value = true;

        var proxy = PojoUtils.getSetterProxy(target.getClass());
        var method = target.getClass().getDeclaredMethod("setBooleanValue", boolean.class);

        // warm up
        for (int i = 0; i < 10; i++) {
            proxy.applyValue(target, fieldName, value);
            method.invoke(target, value);
            target.setBooleanValue(value);
        }

        var started = System.nanoTime();
        for (int i = 0; i < TOTAL; i++) {
            proxy.applyValue(target, fieldName, value);
        }
        var elapsed = System.nanoTime() - started;
        print("SetterProxy", elapsed);

        started = System.nanoTime();
        for (int i = 0; i < TOTAL; i++) {
            method.invoke(target, value);
        }
        elapsed = System.nanoTime() - started;
        print("Reflection", elapsed);

        started = System.nanoTime();
        for (int i = 0; i < TOTAL; i++) {
            target.setBooleanValue(value);
        }
        elapsed = System.nanoTime() - started;
        print("Direct", elapsed);
    }

    protected void print(String type, long elapsed) {
        var df = new DecimalFormat("###,###.##");
        var throughput = (double) TOTAL / elapsed * 1e9;
        System.out.println(
                "[" + type + "] \tElapsed: " + df.format(elapsed / 1e6) + "ms. Throughput: " + df.format(throughput));
    }

}
