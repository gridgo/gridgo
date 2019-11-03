package io.gridgo.bean.test.support;

import java.util.Map;

import io.gridgo.bean.impl.BReferenceBeautifulPrint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@BReferenceBeautifulPrint
public class Foo extends SuperFoo {

    private int intValue;

    private int[] intArrayValue;

    private byte[] byteArrayValue;

    private double doubleValue;

    private String stringValue;

    private byte byteValue;

    private short shortValue;

    private float floatValue;

    private Bar barValue;

    private Map<String, Long> longMap;
}
