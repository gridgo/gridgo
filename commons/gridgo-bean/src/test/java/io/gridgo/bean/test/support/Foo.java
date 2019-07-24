package io.gridgo.bean.test.support;

import io.gridgo.bean.impl.BReferenceBeautifulPrint;
import io.gridgo.utils.annotations.Transient;
import io.gridgo.utils.pojo.FieldName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@BReferenceBeautifulPrint
public class Foo {

    @FieldName("int_value")
    private int i;

    @Transient
    @FieldName("int_array_value")
    private int[] arr;

    @FieldName("double_value")
    private double d;

    @FieldName("string_value")
    private String s;

    @FieldName("bar")
    private Bar b;
}
