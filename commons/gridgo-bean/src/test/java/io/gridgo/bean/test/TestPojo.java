package io.gridgo.bean.test;

import static org.junit.Assert.assertEquals;

import java.text.DecimalFormat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.support.BElementPojoHelper;
import io.gridgo.bean.test.support.Bar;
import io.gridgo.bean.test.support.Foo;
import io.gridgo.bean.test.support.NumberCollectionPojo;
import io.gridgo.bean.test.support.PrimitiveVO;
import io.gridgo.utils.CollectionUtils;
import io.gridgo.utils.MapUtils;
import io.gridgo.utils.ObjectUtils;
import io.gridgo.utils.pojo.PojoUtils;

@SuppressWarnings("deprecation")
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
    @Ignore("Ignore perf test")
    public void testPerfToMap() throws Exception {
        PrimitiveVO vo = PrimitiveVO.builder() //
                .booleanValue(true) //
                .byteValue((byte) 1) //
                .intValue(2) //
                .doubleValue(0.2)//
                .build();

        // warm up
        ObjectUtils.toMapRecursive(vo);
        PojoUtils.toJsonElement(vo);

        long start = 0;

        int testRound = (int) 1e6;
        int numRounds = 10;

        double[] directResults = new double[numRounds];
        double[] objUtilResults = new double[numRounds];
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
                ObjectUtils.toMapRecursive(vo);
            }
            double objUtilsSec = Double.valueOf(System.nanoTime() - start) / 1e9;
            double objUtilsPace = Double.valueOf(testRound) / objUtilsSec;
            objUtilResults[i] = objUtilsPace;

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

        System.out.println("[Object utils toMap] throughput: " + df.format(objUtilsPace) + " ops/s");
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
        ObjectUtils.fromMap(PrimitiveVO.class, bobj.toMap());

        long start = 0;

        int testRound = (int) 1e5;
        int numRounds = 10;

        double[] objUtilResults = new double[numRounds];
        double[] pojoUtilsResults = new double[numRounds];

        for (int i = 0; i < numRounds; i++) {
            start = System.nanoTime();
            for (int j = 0; j < testRound; j++) {
                ObjectUtils.fromMap(PrimitiveVO.class, bobj.toMap());
            }
            double objUtilsSec = Double.valueOf(System.nanoTime() - start) / 1e9;
            double objUtilsPace = Double.valueOf(testRound) / objUtilsSec;
            objUtilResults[i] = objUtilsPace;

            start = System.nanoTime();
            for (int j = 0; j < testRound; j++) {
                bobj.toPojo(PrimitiveVO.class);
            }
            double pojoUtilsSec = Double.valueOf(System.nanoTime() - start) / 1e9;
            double pojoUtilsPace = Double.valueOf(testRound) / pojoUtilsSec;
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

        System.out.println("[Object utils toPojo] throughput: " + df.format(objUtilsPace) + " ops/s");
        System.out.println("[Pojo utils toPojo]   throughput: " + df.format(pojoUtilsPace) + " ops/s");
    }

    @Test
    public void testNumberCollectionPojo() {
        var pojo = NumberCollectionPojo.builder() //
                // list
                .byteList(CollectionUtils.<Byte>newListBuilder() //
                        .add((byte) 1) //
                        .add((byte) 2) //
                        .add((byte) 3) //
                        .build()) //
                .shortList(CollectionUtils.<Short>newListBuilder() //
                        .add((short) 1) //
                        .add((short) 2) //
                        .add((short) 3) //
                        .build()) //
                .integerList(CollectionUtils.<Integer>newListBuilder() //
                        .add((int) 1) //
                        .add((int) 2) //
                        .add((int) 3) //
                        .build()) //
                .longList(CollectionUtils.<Long>newListBuilder() //
                        .add((long) 1) //
                        .add((long) 2) //
                        .add((long) 3) //
                        .build()) //
                .floatList(CollectionUtils.<Float>newListBuilder() //
                        .add((float) 1.0f) //
                        .add((float) 2.1f) //
                        .add((float) 3.2f) //
                        .build()) //
                .doubleList(CollectionUtils.<Double>newListBuilder() //
                        .add((double) 1.0) //
                        .add((double) 2.1) //
                        .add((double) 3.2) //
                        .build()) //
                // set
                .byteSet(CollectionUtils.<Byte>newSetBuilder() //
                        .add((byte) 1) //
                        .add((byte) 2) //
                        .add((byte) 3) //
                        .build()) //
                .shortSet(CollectionUtils.<Short>newSetBuilder() //
                        .add((short) 1) //
                        .add((short) 2) //
                        .add((short) 3) //
                        .build()) //
                .integerSet(CollectionUtils.<Integer>newSetBuilder() //
                        .add((int) 1) //
                        .add((int) 2) //
                        .add((int) 3) //
                        .build()) //
                .longSet(CollectionUtils.<Long>newSetBuilder() //
                        .add((long) 1) //
                        .add((long) 2) //
                        .add((long) 3) //
                        .build()) //
                .floatSet(CollectionUtils.<Float>newSetBuilder() //
                        .add((float) 1.0f) //
                        .add((float) 2.1f) //
                        .add((float) 3.2f) //
                        .build()) //
                .doubleSet(CollectionUtils.<Double>newSetBuilder() //
                        .add((double) 1.0) //
                        .add((double) 2.1) //
                        .add((double) 3.2) //
                        .build()) //
                // map
                .byteMap(MapUtils.<Byte>newMapStringKeyBuilder(Byte.class) //
                        .put("1", (byte) 1) //
                        .put("2", (byte) 2) //
                        .put("3", (byte) 3) //
                        .build()) //
                .shortMap(MapUtils.<Short>newMapStringKeyBuilder(Short.class) //
                        .put("1", (short) 1) //
                        .put("2", (short) 2) //
                        .put("3", (short) 3) //
                        .build()) //
                .integerMap(MapUtils.<Integer>newMapStringKeyBuilder(Integer.class) //
                        .put("1", (int) 1) //
                        .put("2", (int) 2) //
                        .put("3", (int) 3) //
                        .build()) //
                .longMap(MapUtils.<Long>newMapStringKeyBuilder(Long.class) //
                        .put("1", (long) 1) //
                        .put("2", (long) 2) //
                        .put("3", (long) 3) //
                        .build()) //
                .floatMap(MapUtils.<Float>newMapStringKeyBuilder(Float.class) //
                        .put("1", (float) 1) //
                        .put("2", (float) 2) //
                        .put("3", (float) 3) //
                        .build()) //
                .doubleMap(MapUtils.<Double>newMapStringKeyBuilder(Double.class) //
                        .put("1", (double) 1) //
                        .put("2", (double) 2) //
                        .put("3", (double) 3) //
                        .build()) //
                .build();

        var json = BReference.of(pojo).toJson();
        var bObj = BElement.ofJson(json).asObject();
        System.out.println(bObj);

        var rebuiltPojo = bObj.toPojo(NumberCollectionPojo.class);
        assertEquals(pojo, rebuiltPojo);
    }
}
