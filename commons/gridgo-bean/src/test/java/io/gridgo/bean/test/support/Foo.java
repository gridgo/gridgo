package io.gridgo.bean.test.support;

import io.gridgo.bean.impl.BReferenceBeautifulPrint;
import io.gridgo.utils.annotations.Transient;
import io.gridgo.utils.pojo.FieldNameTransform;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@BReferenceBeautifulPrint
@FieldNameTransform(value = "{{fieldName > camelToSnake}}", ignore = { "barValue" })
public class Foo {

    private int intValue;

    @Transient
    private int[] intArrayValue;

    private double doubleValue;

    private String stringValue;

    private Bar barValue;
}
