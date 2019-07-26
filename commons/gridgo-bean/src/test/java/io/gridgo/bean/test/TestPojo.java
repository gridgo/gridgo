package io.gridgo.bean.test;

import static org.junit.Assert.assertEquals;

import java.text.DecimalFormat;

import org.junit.Before;
import org.junit.Test;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.support.BElementPojoHelper;
import io.gridgo.bean.test.support.Bar;
import io.gridgo.bean.test.support.Foo;
import io.gridgo.utils.CollectionUtils;
import io.gridgo.utils.MapUtils;
import io.gridgo.utils.ObjectUtils;

public class TestPojo {

    private Foo original;

    @Before
    public void setup() {
        original = Foo.builder() //
                .intArrayValue(new int[] { 1, 2, 3, 4 }) //
                .doubleValue(0.123) //
                .barValue(Bar.builder() //
                        .b(true) //
                        .build()) //
                .intArrayList(CollectionUtils.newListBuilder(int[].class) //
                        .add(new int[] { 1, 2, 3 }) //
                        .add(new int[] { 5, 7, 6 }) //
                        .build()) //
                .longArrayMap(MapUtils.newMapStringKeyBuilder(long[].class) //
                        .put("longarr1", new long[] { 4l, 5l }) //
                        .put("longarr1", new long[] { 6l, 9l }) //
                        .build()) //
                .barMap((MapUtils.newMapStringKeyBuilder(Bar.class) //
                        .put("key", Bar.builder() //
                                .b(true) //
                                .map(MapUtils.newMapStringKeyBuilder(Integer.class) //
                                        .put("key1", 10) //
                                        .build()) //
                                .build()) //
                        .build())) //
                .build();
    }

    @Test
    public void testSerializePojo() {
        BObject originalAsBObject = BObject.ofPojo(original);
        byte[] bytes = originalAsBObject.toBytes();

        Foo rebuilt = BElementPojoHelper.bObjectToPojo(BElement.ofBytes(bytes), Foo.class);

//        System.out.println("original: " + BReference.of(original));
//        System.out.println("rebuilt : " + BReference.of(rebuilt));

        // convert pojo to bobject to execute equals field by field
        assertEquals(originalAsBObject, BObject.ofPojo(rebuilt));
    }

    @Test
    public void testPerf() {
        int count = (int) 1e5;

        // warm up
        ObjectUtils.toMapRecursive(original);
        BElementPojoHelper.anyToJsonElement(original);

        long start = 0;

        int numRounds = 10;
        double[] objUtilResults = new double[numRounds];
        double[] pojoUtilsResults = new double[numRounds];

        for (int i = 0; i < numRounds; i++) {
            start = System.nanoTime();
            for (int j = 0; j < count; j++) {
                ObjectUtils.toMapRecursive(original);
            }
            double objUtilsSec = Double.valueOf(System.nanoTime() - start) / 1e9;
            double objUtilsPace = Double.valueOf(count) / objUtilsSec;
            objUtilResults[i] = objUtilsPace;

            start = System.nanoTime();
            for (int j = 0; j < count; j++) {
                BElementPojoHelper.anyToJsonElement(original);
            }
            double pojoUtilsSec = Double.valueOf(System.nanoTime() - start) / 1e9;
            double pojoUtilsPace = Double.valueOf(count) / pojoUtilsSec;
            pojoUtilsResults[i] = pojoUtilsPace;
        }

        double objUtilsPace = 0;
        for (int i = 0; i < objUtilResults.length; i++) {
            objUtilsPace += objUtilResults[i];
        }
        objUtilsPace /= objUtilResults.length;

        double pojoUtilsPace = 0;
        for (int i = 0; i < pojoUtilsResults.length; i++) {
            pojoUtilsPace += pojoUtilsResults[i];
        }
        pojoUtilsPace /= pojoUtilsResults.length;

        DecimalFormat df = new DecimalFormat("###,###.##");

        System.out.println("[Object utils] throughput: " + df.format(objUtilsPace) + " ops/s");
        System.out.println("[Pojo utils]   throughput: " + df.format(pojoUtilsPace) + "ops/s");
    }
}
