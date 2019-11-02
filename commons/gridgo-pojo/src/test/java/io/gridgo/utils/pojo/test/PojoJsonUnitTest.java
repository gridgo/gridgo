package io.gridgo.utils.pojo.test;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import io.gridgo.utils.pojo.PojoJsonUtils;
import io.gridgo.utils.pojo.test.support.BooleanVO;
import io.gridgo.utils.pojo.test.support.PrimitiveArrayVO;
import io.gridgo.utils.pojo.test.support.PrimitiveVO;

public class PojoJsonUnitTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testBooleanField1() {
        BooleanVO vo = new BooleanVO();
        var jsonElement = (Map<String, Object>) PojoJsonUtils.toJsonElement(vo);
        Assert.assertEquals(false, jsonElement.get("is_boolean_value1"));
        Assert.assertEquals(false, jsonElement.get("boolean_value2"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testToJsonElementPrimitive() throws Exception {
        PrimitiveVO vo = PrimitiveVO.builder() //
                .booleanValue(true) //
                .byteValue((byte) 1) //
                .intValue(2) //
                .doubleValue(0.2)//
                .build();
        var json = (Map<String, Object>) PojoJsonUtils.toJsonElement(vo);
        Assert.assertEquals(2, json.get("intValue"));
        Assert.assertEquals(0.0f, json.get("floatValue"));
        Assert.assertEquals((short) 0, json.get("shortValue"));
        Assert.assertEquals(0.2, json.get("doubleValue"));
        Assert.assertEquals(0L, json.get("longValue"));
        Assert.assertEquals((byte) 1, json.get("byteValue"));
        Assert.assertEquals(true, json.get("booleanValue"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testToJsonElementArray() throws Exception {
        PrimitiveArrayVO vo = PrimitiveArrayVO.builder() //
                .intValue(new int[] {1, 2, 3, 4}) //
                .intListValue(Arrays.asList(1, 2, 3, 4))
                .intMapValue(Collections.singletonMap("k1", 1))
                .build();
        var json = (Map<String, Object>) PojoJsonUtils.toJsonElement(vo);
        Assert.assertEquals(json.get("intValue"), Arrays.asList(1, 2, 3, 4));
        Assert.assertEquals(json.get("intListValue"), Arrays.asList(1, 2, 3, 4));
        Assert.assertEquals(json.get("intMapValue"), Collections.singletonMap("k1", 1));
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
        PojoJsonUtils.toJsonElement(vo);

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
                PojoJsonUtils.toJsonElement(vo);
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
}
