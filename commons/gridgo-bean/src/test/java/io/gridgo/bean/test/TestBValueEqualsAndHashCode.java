package io.gridgo.bean.test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import io.gridgo.bean.BValue;
import io.gridgo.utils.hash.BinaryHashCodeCalculator;

public class TestBValueEqualsAndHashCode {

    // @Test
    public void benchmarkHashCode()
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        List<String> list = new LinkedList<>();
        for (int i = 0; i < 100000; i++) {
            list.add("alsdhfla snfoiwufo sdoifhasp faofh oaish ashf" + i);
        }

        // using default String.hashCode()
        long startTime = System.nanoTime();
        for (String str : list) {
            str.hashCode();
        }
        long time1 = System.nanoTime() - startTime;

        // using XXHASH32
        startTime = System.nanoTime();
        for (String str : list) {
            BinaryHashCodeCalculator.XXHASH32_JNI.calcHashCode(str.getBytes());
        }
        long time2 = System.nanoTime() - startTime;

        // optimize using reflection
        var valueField = String.class.getDeclaredField("value");
        valueField.setAccessible(true);
        startTime = System.nanoTime();
        for (String str : list) {
            BinaryHashCodeCalculator.XXHASH32_JNI.calcHashCode((byte[]) valueField.get(str));
        }
        long time3 = System.nanoTime() - startTime;

        System.out.println("time1 = " + time1);
        System.out.println("time2 = " + time2);
        System.out.println("time3 = " + time3);

        System.out.println("time2 - time1 = " + (time2 - time1));
        System.out.println("time3 - time1 = " + (time3 - time1));
    }

    @Test
    public void testEqualsAndHashCode() {
        Map<BValue, Object> map = new HashMap<>();
        map.put(BValue.of(true), "true");
        map.put(BValue.of(false), "false");
        map.put(BValue.of((int) 1), "int");
        map.put(BValue.of((float) 1.0), "float");
        map.put(BValue.of('a'), "char");

        map.put(BValue.of("str"), "str");
        map.put(BValue.of(new byte[] { 0, 1, 2, 3, 4, 5 }), "raw");

        assertEquals("true", map.get(BValue.of(true)));
        assertEquals("false", map.get(BValue.of(false)));

        assertEquals("int", map.get(BValue.of((int) 1)));
        assertEquals("float", map.get(BValue.of((float) 1.0)));

        assertEquals("char", map.get(BValue.of(BValue.of('a'))));
        assertEquals("str", map.get(BValue.of("str")));

        assertEquals("raw", map.get(BValue.of(new byte[] { 0, 1, 2, 3, 4, 5 })));
    }
}
