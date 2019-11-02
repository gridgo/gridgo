package io.gridgo.utils.pojo.test.support;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrimitiveVO {

    private boolean booleanValue;

    private char charValue;

    private byte byteValue;

    private short shortValue;

    private int intValue;

    private long longValue;

    private float floatValue;

    private double doubleValue;

    private String stringValue;

    public Map<String, Object> toMap() {
        var map = new HashMap<String, Object>();
        map.put("booleanValue", booleanValue);
        map.put("charValue", charValue);
        map.put("byteValue", byteValue);
        map.put("shortValue", shortValue);
        map.put("intValue", intValue);
        map.put("longValue", longValue);
        map.put("floatValue", floatValue);
        map.put("doubleValue", doubleValue);
        map.put("stringValue", stringValue);
        return map;
    }
}
