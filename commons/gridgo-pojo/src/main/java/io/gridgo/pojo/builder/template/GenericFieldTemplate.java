package io.gridgo.pojo.builder.template;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import io.gridgo.otac.OtacAccessLevel;
import io.gridgo.otac.OtacMethod;
import io.gridgo.otac.OtacParameter;
import io.gridgo.otac.code.block.OtacBlock;
import io.gridgo.otac.code.block.OtacForeach;
import io.gridgo.otac.code.line.OtacLine;
import io.gridgo.otac.value.OtacValue;
import io.gridgo.pojo.reflect.type.PojoParameterizedType;
import io.gridgo.pojo.reflect.type.PojoType;
import io.gridgo.utils.pojo.exception.PojoException;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class GenericFieldTemplate extends BuilderAwareTemplate<OtacMethod> {

    private static final String arg0 = "input";

    private static final AtomicInteger TEMP_ID_SEED = new AtomicInteger(0);

    private final @NonNull PojoType rootType;

    @Override
    public OtacMethod apply() {

        var builder = OtacMethod.builder() //
                .accessLevel(OtacAccessLevel.PRIVATE) //
                .name("_temp_" + TEMP_ID_SEED.getAndIncrement()) //
                .parameter(OtacParameter.parameter(rootType.rawType(), arg0)) //
                .body(build(rootType).getLines());

        var rawType = rootType.rawType();
        if (rawType.isPrimitive()) 
            throw new PojoException("Non-generic type cannot be processed" + rawType);
        
        var otacMethod = builder.build();
        getClassBuilder().method(otacMethod);
        return otacMethod;
    }

    protected OtacBlock build(PojoType type) {
        if (type instanceof PojoParameterizedType)
            return buildParameterizedType((PojoParameterizedType) type);
        return buildRaw(type.rawType());
    }

    private OtacBlock buildParameterizedType(PojoParameterizedType parameterizedType) {
        var rawType = parameterizedType.rawType();
        if (Collection.class.isAssignableFrom(rawType))
            return buildCollection(parameterizedType.actualTypeArguments().get(0));
        if (Map.class.isAssignableFrom(rawType)) {
            if (parameterizedType.actualTypeArguments().get(0).rawType() != String.class)
                throw new PojoException("Map's first generic type must be String.");
            return buildMap(parameterizedType.actualTypeArguments().get(1));
        }
        return null;
    }

    private OtacBlock buildCollection(PojoType elementType) {
        var method = GenericFieldTemplate.builder() //
                .rootType(elementType) //
                .builder(getBuilder()) //
                .build() //
                .apply();
        var result = OtacBlock.builder() //
                .addLine(OtacLine.declare("i", 0)) //
                .addLine(OtacForeach.builder() //
                        .variableName("entry") //
                        .sequence(OtacValue.variable("collection")) //
                        .build()) //
                .addLine(OtacLine.invokeMethod(method.getName())) //
                .addLine(OtacLine.customLine("i++"));
        return result.build();
    }

    private OtacBlock buildMap(PojoType elementType) {
        return null;
    }

    private OtacBlock buildRaw(Class<?> rawType) {
        return null;
    }
}
