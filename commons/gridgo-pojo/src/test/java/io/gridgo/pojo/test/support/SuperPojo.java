package io.gridgo.pojo.test.support;

import io.gridgo.pojo.annotation.FieldTag;
import lombok.Data;

@Data
public class SuperPojo {

    @FieldTag(name = "string_field", tag = "snake")
    @FieldTag(name = "stringField", tag = "camel")
    private String superStringField;
}
