package io.gridgo.pojo.test.support;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Bar extends Foo<Character> {

    private String name;

    private int[] data3;
    
    private Primitive[] data4;

}
