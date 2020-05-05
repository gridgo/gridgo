package io.gridgo.pojo.test.support;

import io.gridgo.pojo.annotation.FieldTag;
import lombok.Data;

@Data
public class Super {

    @FieldTag(name = "string_field", tag = "snake")
    @FieldTag(name = "stringField", tag = "camel")
    private String stringField;

    private String[] stringArray;
}
