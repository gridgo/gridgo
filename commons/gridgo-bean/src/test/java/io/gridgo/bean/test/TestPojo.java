package io.gridgo.bean.test;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.assertEquals;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.support.BElementPojoHelper;
import io.gridgo.bean.test.support.Bar;
import io.gridgo.bean.test.support.Foo;
import io.gridgo.bean.test.support.NumberCollectionPojo;
import io.gridgo.bean.test.support.PrimitiveVO;
import io.gridgo.utils.pojo.PojoUtils;

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
                .intArrayList(Arrays.asList( //
                        new int[] { 1, 2, 3 }, //
                        new int[] { 5, 7, 6 })) //
                .longArrayMap(Map.of( //
                        "longarr1", new long[] { 4l, 5l }, //
                        "longarr1", new long[] { 6l, 9l })) //
                .barMap(Map.of( //
                        "key", Bar.builder() //
                                .b(true) //
                                .map(Map.of("key1", 10)) //
                                .build())) //
                .build();
    }

    @Test
    public void testSerializePojo() {
        BObject originalAsBObject = BObject.ofPojo(original);
        byte[] bytes = originalAsBObject.toBytes();

        Foo rebuilt = BElementPojoHelper.bObjectToPojo(BElement.ofBytes(bytes), Foo.class);

        // convert pojo to bobject to execute equals field by field
        assertEquals(originalAsBObject, BObject.ofPojo(rebuilt));
    }

    @Test
    @Ignore("Ignore perf test")
    public void testPerfToMap() throws Exception {
        PrimitiveVO vo = PrimitiveVO.builder() //
                .booleanValue(true) //
                .byteValue((byte) 1) //
                .intValue(2) //
                .doubleValue(0.2)//
                .build();

        // warm up
        PojoUtils.toJsonElement(vo);

        long start = 0;

        int testRound = (int) 1e6;
        int numRounds = 10;

        double[] directResults = new double[numRounds];
        double[] pojoUtilsResults = new double[numRounds];

        for (int i = 0; i < numRounds; i++) {
            start = System.nanoTime();
            for (int j = 0; j < testRound; j++) {
                vo.toMap();
            }
            double directSec = Double.valueOf(System.nanoTime() - start) / 1e9;
            double directPace = Double.valueOf(testRound) / directSec;
            directResults[i] = directPace;

            start = System.nanoTime();
            for (int j = 0; j < testRound; j++) {
                PojoUtils.toJsonElement(vo);
            }
            double pojoUtilsSec = Double.valueOf(System.nanoTime() - start) / 1e9;
            double pojoUtilsPace = Double.valueOf(testRound) / pojoUtilsSec;
            pojoUtilsResults[i] = pojoUtilsPace;
        }

        double directPace = 0;
        for (int i = 0; i < directResults.length; i++) {
            directPace += directResults[i];
        }
        directPace /= directResults.length;

        double pojoUtilsPace = 0;
        for (int i = 0; i < pojoUtilsResults.length; i++) {
            pojoUtilsPace += pojoUtilsResults[i];
        }
        pojoUtilsPace /= pojoUtilsResults.length;

        DecimalFormat df = new DecimalFormat("###,###.##");

        System.out.println("[Pojo utils toMap]   throughput: " + df.format(pojoUtilsPace) + " ops/s");
        System.out.println("[Direct toMap]       throughput: " + df.format(directPace) + " ops/s");
    }

    @Test
    @Ignore("Ignore pert test")
    public void testPerfToPojo() throws Exception {
        PrimitiveVO vo = PrimitiveVO.builder() //
                .booleanValue(true) //
                .byteValue((byte) 1) //
                .intValue(2) //
                .doubleValue(0.2)//
                .build();

        // warm up
        var bobj = BObject.ofPojo(vo);
        bobj.toPojo(PrimitiveVO.class);

        long start = 0;

        int testRound = (int) 1e5;
        int numRounds = 10;

        double[] pojoUtilsResults = new double[numRounds];

        for (int i = 0; i < numRounds; i++) {
            start = System.nanoTime();
            for (int j = 0; j < testRound; j++) {
                bobj.toPojo(PrimitiveVO.class);
            }
            double pojoUtilsSec = Double.valueOf(System.nanoTime() - start) / 1e9;
            double pojoUtilsPace = Double.valueOf(testRound) / pojoUtilsSec;
            pojoUtilsResults[i] = pojoUtilsPace;
        }

        double pojoUtilsPace = 0;
        for (int i = 0; i < pojoUtilsResults.length; i++) {
            pojoUtilsPace += pojoUtilsResults[i];
        }
        pojoUtilsPace /= pojoUtilsResults.length;

        DecimalFormat df = new DecimalFormat("###,###.##");

        System.out.println("[Pojo utils toPojo]   throughput: " + df.format(pojoUtilsPace) + " ops/s");
    }

    @Test
    public void testNumberCollectionPojo() {
        var pojo = NumberCollectionPojo.builder() //
                // list
                .byteList(Arrays.asList( //
                        (byte) 1, //
                        (byte) 2, //
                        (byte) 3)) //
                .shortList(Arrays.asList( //
                        (short) 1, //
                        (short) 2, //
                        (short) 3)) //
                .integerList(Arrays.asList( //
                        1, //
                        2, //
                        3)) //
                .longList(Arrays.asList( //
                        (long) 1, //
                        (long) 2, //
                        (long) 3)) //
                .floatList(Arrays.asList( //
                        1.0f, //
                        2.1f, //
                        3.2f)) //
                .doubleList(Arrays.asList( //
                        1.0, //
                        2.1, //
                        3.2)) //
                // set
                .byteSet(new HashSet<>(Arrays.asList( //
                        (byte) 1, //
                        (byte) 2, //
                        (byte) 3))) //
                .shortSet(new HashSet<>(Arrays.asList( //
                        (short) 1, //
                        (short) 2, //
                        (short) 3))) //
                .integerSet(new HashSet<>(Arrays.asList( //
                        1, //
                        2, //
                        3))) //
                .longSet(new HashSet<>(Arrays.asList( //
                        (long) 1, //
                        (long) 2, //
                        (long) 3))) //
                .floatSet(new HashSet<>(Arrays.asList( //
                        1.0f, //
                        2.1f, //
                        3.2f))) //
                .doubleSet(new HashSet<>(Arrays.asList( //
                        1.0, //
                        2.1, //
                        3.2))) //
                // map
                .byteMap(Map.of(
                        "1", (byte) 1, //
                        "2", (byte) 2, //
                        "3", (byte) 3)) //
                .shortMap(Map.of(
                        "1", (short) 1, //
                        "2", (short) 2, //
                        "3", (short) 3)) //
                .integerMap(Map.of(
                        "1", 1, //
                        "2", 2, //
                        "3", 3)) //
                .longMap(Map.of( //
                        "1", (long) 1, //
                        "2", (long) 2, //
                        "3", (long) 3)) //
                .floatMap(Map.of( //
                        "1", (float) 1, //
                        "2", (float) 2, //
                        "3", (float) 3)) //
                .doubleMap(Map.of( //
                        "1", (double) 1, //
                        "2", (double) 2, //
                        "3", (double) 3)) //
                .build();

        var json = BReference.of(pojo).toJson();
        var bObj = BElement.ofJson(json).asObject();
        System.out.println(bObj);

        var rebuiltPojo = bObj.toPojo(NumberCollectionPojo.class);
        assertEquals(pojo, rebuiltPojo);
    }
}
