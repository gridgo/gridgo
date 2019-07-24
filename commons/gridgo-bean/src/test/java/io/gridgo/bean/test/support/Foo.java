package io.gridgo.bean.test.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Foo {

    private int i;

    private int[] arr;

    private double d;

    private String s;

    private Bar b;
}
