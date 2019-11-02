package io.gridgo.utils.pojo.test.support;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrimitiveArrayVO {

    private boolean[] booleanValue;
    private char[] charValue;
    private byte[] byteValue;
    private short[] shortValue;
    private int[] intValue;
    private long[] longValue;
    private float[] floatValue;
    private double[] doubleValue;
    private List<Integer> intListValue;
    private Map<String, Integer> intMapValue;
    private String[] stringValue;
}
