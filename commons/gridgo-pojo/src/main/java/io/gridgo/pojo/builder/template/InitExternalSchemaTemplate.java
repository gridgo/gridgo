package io.gridgo.pojo.builder.template;

import io.gridgo.otac.OtacAccessLevel;
import io.gridgo.otac.OtacField;
import io.gridgo.otac.OtacType;
import io.gridgo.otac.code.line.OtacLine;
import io.gridgo.otac.value.OtacValue;
import io.gridgo.pojo.PojoSchema;
import io.gridgo.pojo.PojoSchemaConfig;
import io.gridgo.pojo.builder.PojoSchemaBuilder;
import io.gridgo.utils.StringUtils;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class InitExternalSchemaTemplate extends BuilderAwareTemplate<String> {

    private final @NonNull String fieldName;

    private final @NonNull Class<?> fieldType;

    @Override
    public String apply() {

        var schemaFieldName = "_" + StringUtils.lowerCaseFirstLetter(fieldName) + "_schema";

        getClassBuilder().field(OtacField.builder() //
                .name(schemaFieldName) //
                .accessLevel(OtacAccessLevel.PRIVATE) //
                .type(OtacType.typeOf(PojoSchema.class)) //
                .build());

        getClassBuilder().require(PojoSchemaBuilder.class) //
                .require(fieldType) //
                .require(PojoSchemaConfig.class);

        getInitMethodBuilder().addLine(OtacLine.assignField(schemaFieldName,
                OtacValue.customValue("new " + PojoSchemaBuilder.class.getSimpleName() + "(" + fieldType.getSimpleName()
                        + ".class, " + PojoSchemaConfig.class.getSimpleName() + ".DEFAULT).build()")));

        return schemaFieldName;
    }

}
