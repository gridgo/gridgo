package io.gridgo.pojo.builder;

import static io.gridgo.utils.StringUtils.lowerCaseFirstLetter;

public class PojoConvension {

    public static final String CASTED_TARGET_VARNAME = "castedTarget";
    public static final String SCHEMA_OUTPUT_VARNAME = "schemaOutput";
    public static final String SEQUENCE_OUTPUT_VARNAME = "sequenceOutput";

    public static String genFieldNameKey(String fieldName) {
        return "_" + fieldName + "_key";
    }

    public static String genExtSchemaName(Class<?> fieldType) {
        return "_" + lowerCaseFirstLetter(fieldType.getName().replace(".", "_")) + "_schema";
    }
}
