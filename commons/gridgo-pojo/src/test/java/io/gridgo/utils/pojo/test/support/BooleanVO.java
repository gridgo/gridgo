package io.gridgo.utils.pojo.test.support;

import io.gridgo.utils.pojo.FieldName;
import io.gridgo.utils.pojo.FieldNameTransform;
import lombok.Data;

@Data
@FieldNameTransform("{{fieldName > camelToSnake}}")
public class BooleanVO {

    @FieldName("is_boolean_value1")
    private boolean isBooleanValue1;

    private Boolean isBooleanValue2;

    private boolean booleanValue2;
}
