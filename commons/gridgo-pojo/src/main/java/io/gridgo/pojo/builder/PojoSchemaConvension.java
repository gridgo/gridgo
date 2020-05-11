package io.gridgo.pojo.builder;

public class PojoSchemaConvension {

    public static final String CASTED_TARGET_VARNAME = "castedTarget";
    public static final String SCHEMA_OUTPUT_VARNAME = "schemaOutput";
    public static final String SEQUENCE_OUTPUT_VARNAME = "sequenceOutput";

    public static String genFieldNameKey(String fieldName) {
        return "_" + fieldName + "_key_";
    }
}
