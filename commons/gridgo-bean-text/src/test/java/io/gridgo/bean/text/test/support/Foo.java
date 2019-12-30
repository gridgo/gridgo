package io.gridgo.bean.text.test.support;

import java.util.Map;

import io.gridgo.bean.impl.BReferenceBeautifulPrint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@BReferenceBeautifulPrint
@EqualsAndHashCode(callSuper = true)
public class Foo extends SuperFoo {

    private int intValue;

    private int[] intArrayValue;

    private long[] longArrayValue;

    private byte[] byteArrayValue;

    private double doubleValue;

    private String stringValue;

    // private byte byteValue;

    private short shortValue;

    private float floatValue;

    private Bar barValue;

    private Map<String, Long> longMap;
}
