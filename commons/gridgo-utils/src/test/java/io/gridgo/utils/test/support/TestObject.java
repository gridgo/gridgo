package io.gridgo.utils.test.support;

import java.util.Map;

import lombok.Data;

@Data
public class TestObject {

    private int testInt;

    private boolean testBool;

    private String testStr;

    private TestObject testObj;

    private Map<String, Object> testMap;

    private int[] testArr;
}
