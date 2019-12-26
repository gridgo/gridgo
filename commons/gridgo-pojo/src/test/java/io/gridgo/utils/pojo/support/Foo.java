package io.gridgo.utils.pojo.support;

import java.util.Date;

import io.gridgo.utils.pojo.FieldName;
import io.gridgo.utils.pojo.FieldNameTransform;
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
@FieldNameTransform(value = "{{fieldName > camelToSnake}}")
public class Foo extends SuperFoo {

    private int intValue;

    // @Transient
    private int[] intArrayValue;

    private double doubleValue;

    @FieldName("string_value_override")
    private String stringValue;

    private Bar barValue;
    
    private Date date;
}
