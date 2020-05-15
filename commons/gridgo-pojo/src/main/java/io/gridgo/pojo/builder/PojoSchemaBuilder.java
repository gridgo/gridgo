package io.gridgo.pojo.builder;

import static io.gridgo.otac.value.OtacValue.NULL;
import static io.gridgo.pojo.builder.PojoConvension.SCHEMA_OUTPUT_VARNAME;
import static io.gridgo.pojo.builder.PojoConvension.SEQUENCE_OUTPUT_VARNAME;
import static io.gridgo.utils.StringUtils.upperCaseFirstLetter;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;

import io.gridgo.otac.OtacAccessLevel;
import io.gridgo.otac.OtacClass;
import io.gridgo.otac.OtacClass.OtacClassBuilder;
import io.gridgo.otac.OtacConstructor;
import io.gridgo.otac.OtacConstructor.OtacConstructorBuilder;
import io.gridgo.otac.OtacField;
import io.gridgo.otac.OtacGeneric;
import io.gridgo.otac.OtacMethod;
import io.gridgo.otac.OtacMethod.OtacMethodBuilder;
import io.gridgo.otac.OtacParameter;
import io.gridgo.otac.OtacType;
import io.gridgo.otac.code.OtacCodeElement;
import io.gridgo.otac.code.block.OtacBlock;
import io.gridgo.otac.code.block.OtacElse;
import io.gridgo.otac.code.block.OtacFor;
import io.gridgo.otac.code.block.OtacForeach;
import io.gridgo.otac.code.block.OtacIf;
import io.gridgo.otac.code.block.OtacTry;
import io.gridgo.otac.code.line.OtacLine;
import io.gridgo.otac.value.OtacCastedValue;
import io.gridgo.otac.value.OtacValue;
import io.gridgo.pojo.PojoSchema;
import io.gridgo.pojo.PojoSchemaConfig;
import io.gridgo.pojo.annotation.FieldTag;
import io.gridgo.pojo.output.PojoOutput;
import io.gridgo.pojo.output.PojoSchemaOutput;
import io.gridgo.pojo.output.PojoSequenceOutput;
import io.gridgo.pojo.reflect.PojoReflectiveGetter;
import io.gridgo.pojo.reflect.PojoReflectiveSetter;
import io.gridgo.pojo.reflect.PojoReflectiveStruct;
import io.gridgo.pojo.reflect.type.PojoArrayType;
import io.gridgo.pojo.reflect.type.PojoParameterizedType;
import io.gridgo.pojo.reflect.type.PojoSimpleType;
import io.gridgo.pojo.reflect.type.PojoType;
import io.gridgo.pojo.support.GenericPojoSchemaHelper;
import io.gridgo.utils.pojo.exception.PojoException;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class PojoSchemaBuilder<T> {

    private final @NonNull Class<T> type;

    private final @NonNull PojoSchemaConfig config;

    private final OtacClassBuilder<?, ?> classBuilder;

    private final OtacConstructorBuilder<?, ?> constructorBuilder;

    private final OtacMethodBuilder<?, ?> serializeMethodBuilder;

    private final OtacMethodBuilder<?, ?> initMethodBuilder;

    private final AtomicInteger idSeed = new AtomicInteger(0);

    private final Set<Class<?>> alreadyInitExtSchemas = new HashSet<>();

    public PojoSchemaBuilder(Class<T> type, PojoSchemaConfig config) {
        this.type = type;
        this.config = config;

        classBuilder = OtacClass.builder() //
                .accessLevel(OtacAccessLevel.PUBLIC) //
                .packageName(type.getPackageName()) //
                .simpleClassName(type.getSimpleName() + "_generatedSchema___") //
                .extendsFrom(OtacType.explicitlyBuilder() //
                        .type(AbstractPojoSchema.class) //
                        .genericType(OtacGeneric.generic(type)) //
                        .build());

        constructorBuilder = OtacConstructor.builder() //
                .accessLevel(OtacAccessLevel.PUBLIC) //
                .parameter(OtacParameter.parameter(Class.class, "type")) //
                .addLine(OtacLine.invokeMethod("super", OtacValue.variable("type")));

        serializeMethodBuilder = OtacMethod.builder() //
                .accessLevel(OtacAccessLevel.PUBLIC) //
                .name("serialize") //
                .parameter(OtacParameter.parameter(OtacType.OBJECT, "rawTarget")) //
                .parameter(OtacParameter.parameter(PojoSchemaOutput.class, "output")) //
                .addLine(OtacLine.declare(type, "target", OtacValue.castVariable("rawTarget", type))) //
                .addLine(OtacLine.declare(PojoSchemaOutput.class, SCHEMA_OUTPUT_VARNAME, NULL)) //
                .addLine(OtacLine.declare(PojoSequenceOutput.class, SEQUENCE_OUTPUT_VARNAME, NULL));

        initMethodBuilder = OtacMethod.builder() //
                .accessLevel(OtacAccessLevel.PROTECTED)//
                .name("init");
    }

    private OtacMethodBuilder<?, ?> generateTempMethod() {
        int id = idSeed.getAndIncrement();
        return OtacMethod.builder() //
                .accessLevel(OtacAccessLevel.PRIVATE) //
                .name("__temp_method_" + id);
    }

    @SuppressWarnings("unchecked")
    public PojoSchema<T> build() {

        var includeDefault = config.isIncludeDefault();
        var includingTags = config.getIncludeTags();

        var struct = PojoReflectiveStruct.of(type);
        var getters = filterGetters(struct.getters(), includeDefault, includingTags);
        var setters = filterSetters(struct.setters(), includeDefault, includingTags);

        makeClass(getters, setters);

        classBuilder.constructor(constructorBuilder.build());
        classBuilder.method(initMethodBuilder.build());
        classBuilder.method(serializeMethodBuilder.build());

        var cls = compile();

        try {
            var instance = (AbstractPojoSchema<T>) cls.getConstructor(Class.class).newInstance(type);
            instance.init();
            return instance;
        } catch (InstantiationException //
                | IllegalAccessException //
                | IllegalArgumentException //
                | InvocationTargetException //
                | NoSuchMethodException //
                | SecurityException e) {
            throw new PojoException("Cannot init schema instance: " + cls.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private Class<T> compile() {
        var otacClass = classBuilder.build();
        try {
            System.out.println(otacClass.printWithLineNumber() + "\n--------------");
            var compiler = new SimpleCompiler();
            compiler.cook(otacClass.toString());
            return (Class<T>) compiler.getClassLoader().loadClass(otacClass.getName());
        } catch (CompileException | ClassNotFoundException e) {
            throw new PojoException("Cannot build schema: " + otacClass.getName(), e);
        }
    }

    private Map<String, PojoReflectiveSetter> filterSetters( //
            List<PojoReflectiveSetter> setters, //
            boolean includeDefault, //
            Set<String> includingTags) {

        var filteredSetters = new HashMap<String, PojoReflectiveSetter>();
        for (var setter : setters) {
            for (var fieldTag : setter.tags()) {
                if (includingTags.contains(fieldTag.tag()) //
                        || (includeDefault && fieldTag.tag().isBlank())) {

                    var old = filteredSetters.putIfAbsent(setter.fieldName(), setter);
                    if (old != null && (old.element().isField() && setter.element().isMethod()))
                        filteredSetters.put(setter.fieldName(), setter);
                }
            }
        }

        return filteredSetters;
    }

    private Map<String, PojoReflectiveGetter> filterGetters( //
            List<PojoReflectiveGetter> getters, //
            boolean includeDefault, //
            Set<String> includingTags) {//

        var filteredGetters = new HashMap<String, PojoReflectiveGetter>();
        for (var getter : getters) {
            FieldTag[] fieldTags = getter.tags();
            if (fieldTags.length == 0 && includeDefault) {
                var old = filteredGetters.putIfAbsent(getter.fieldName(), getter);
                if (old != null && (old.element().isField() && getter.element().isMethod()))
                    filteredGetters.put(getter.fieldName(), getter);
            } else
                for (var fieldTag : fieldTags) {
                    var tag = fieldTag.tag();
                    if (includingTags.contains(tag) || (includeDefault && tag.isBlank())) {
                        var old = filteredGetters.putIfAbsent(getter.fieldName(), getter);
                        if (old != null && (old.element().isField() && getter.element().isMethod()))
                            filteredGetters.put(getter.fieldName(), getter);
                    }
                }
        }
        return filteredGetters;
    }

    private void makeClass(Map<String, PojoReflectiveGetter> getters, Map<String, PojoReflectiveSetter> setters) {

        var otacByteArrayType = OtacType.typeOf(byte[].class);

        for (var getter : getters.values()) {
            var fieldName = getter.fieldName();
            var fieldNameKey = PojoConvension.genFieldNameKey(fieldName);

            classBuilder.field(OtacField.builder() //
                    .accessLevel(OtacAccessLevel.PRIVATE) //
                    .isFinal(true) //
                    .type(otacByteArrayType) //
                    .name(fieldNameKey) //
                    .initValue(OtacValue.methodReturn(OtacValue.raw(fieldName), "getBytes")) //
                    .build());

            var line = processGetter(getter);
            if (line != null)
                serializeMethodBuilder.addLine(line);
        }
    }

    private OtacCodeElement processGetter(PojoReflectiveGetter getter) {
        var fieldName = getter.fieldName();
        var fieldType = getter.fieldType();
        var methodName = getter.element().method().getName();
        var localValue = "_" + fieldName;
        var outputKey = PojoConvension.genFieldNameKey(fieldName);
        return OtacBlock.builder() //
                .wrapped(false) //
                .addLine(OtacLine.declare( //
                        OtacType.customType(fieldType.getSimpleName()), //
                        localValue, //
                        OtacValue.methodReturn(OtacValue.variable("target"), methodName)))
                .addLine(handleAnyType(localValue, OtacValue.field(outputKey), fieldType, true)) //
                .build();
    }

    private OtacCodeElement handleAnyType(String localValueName, OtacValue outputKey, PojoType type) {
        return handleAnyType(localValueName, outputKey, type, false);
    }

    private OtacCodeElement handleAnyType(String localValueName, OtacValue outputKey, PojoType type, boolean isRoot) {
        if (type.isSimple()) {
            var simpleType = type.asSimple();
            var fieldType = simpleType.type();

            if (Iterable.class.isAssignableFrom(fieldType)) {
                var parameterizedType = PojoParameterizedType.builder() //
                        .rawType(fieldType) //
                        .actualTypeArgument(PojoSimpleType.OBJECT) //
                        .build();
                return handleParameterized(localValueName, outputKey, parameterizedType, isRoot);
            }

            if (Map.class.isAssignableFrom(fieldType)) {
                var parameterizedType = PojoParameterizedType.builder() //
                        .rawType(fieldType) //
                        .actualTypeArgument(PojoSimpleType.of(String.class)) //
                        .actualTypeArgument(PojoSimpleType.OBJECT) //
                        .build();
                return handleParameterized(localValueName, outputKey, parameterizedType, isRoot);
            }

            if (!PojoOutput.isDirectSupported(fieldType) && !simpleType.isWrapper()
                    && !alreadyInitExtSchemas.contains(fieldType)) {
                var schemaFieldName = PojoConvension.genExtSchemaName(fieldType);
                getClassBuilder() //
                        .field(OtacField.builder() //
                                .name(schemaFieldName) //
                                .accessLevel(OtacAccessLevel.PRIVATE) //
                                .type(OtacType.typeOf(PojoSchema.class)) //
                                .build());

                getInitMethodBuilder() //
                        .addLine(OtacLine.assignField(//
                                schemaFieldName, //
                                OtacValue.methodReturn( //
                                        "createSchema", //
                                        OtacValue.ofClass(fieldType), //
                                        OtacValue.staticField(PojoSchemaConfig.class, "DEFAULT"))));

                alreadyInitExtSchemas.add(fieldType);

            }
            getClassBuilder().require(simpleType.type());
            return SimpleTypeHandler.builder() //
                    .isRoot(isRoot) //
                    .fieldType(simpleType) //
                    .localValueName(localValueName) //
                    .outputKey(outputKey) //
                    .build() //
                    .genCode();
        }

        if (type.isArray()) {
            var arrayType = type.asArray();
            return handleArray(localValueName, outputKey, arrayType, isRoot);
        }

        if (type.isParameterized()) {
            var parameterizedType = type.asParameterized();
            getClassBuilder().require(parameterizedType.rawType());
            return handleParameterized(localValueName, outputKey, parameterizedType, isRoot);
        }

        return null;
    }

    private OtacCodeElement handleParameterized(String localValueName, OtacValue outputKey, PojoParameterizedType type,
            boolean isRoot) {
        var rawType = type.rawType();
        var typeArgs = type.actualTypeArguments();
        var firstTypeArg = typeArgs.isEmpty() ? PojoSimpleType.OBJECT : typeArgs.get(0);
        if (Iterable.class.isAssignableFrom(rawType)) {
            return handleIterable(localValueName, outputKey, firstTypeArg, isRoot);
        } else if (Map.class.isAssignableFrom(rawType)) {
            return handleMap(localValueName, outputKey, firstTypeArg,
                    typeArgs.size() > 1 ? typeArgs.get(1) : PojoSimpleType.OBJECT, isRoot);
        }
        throw new IllegalArgumentException("Unsupported type " + type.getSimpleName());
    }

    private OtacCodeElement handleIterable(String localValueName, OtacValue outputKey, PojoType elementType,
            boolean isRoot) {
        var output = OtacValue.variable("output");
        var localValue = OtacValue.variable(localValueName);
        if (elementType.isSimple() && elementType.asSimple().type() == Object.class) {
            return OtacLine.invokeMethod(OtacValue.ofType(GenericPojoSchemaHelper.class), "serializeSequence",
                    outputKey, localValue, output);
        }

        var methodName = buildMethodForIterable(elementType);
        var subSeqOutput = OtacValue.variable(SEQUENCE_OUTPUT_VARNAME);
        var initSubOutput = isRoot //
                ? OtacLine.assignVariable(//
                        SEQUENCE_OUTPUT_VARNAME, //
                        OtacValue.methodReturn( //
                                output, //
                                "openSequence", //
                                outputKey)) //
                : OtacLine.declare(//
                        PojoSequenceOutput.class, //
                        SEQUENCE_OUTPUT_VARNAME, //
                        OtacValue.methodReturn( //
                                output, //
                                "openSequence", //
                                outputKey));

        return OtacIf.builder() //
                .condition(OtacLine.customLine(localValueName + "  == null")) //
                .addLine(OtacLine.invokeMethod(output, "writeNull", outputKey)) //
                .orElse(OtacElse.builder() //
                        .addLine(initSubOutput) //
                        .addLine(OtacTry.builder() //
                                .addLine(OtacLine.invokeMethod( //
                                        methodName, //
                                        localValue, //
                                        subSeqOutput)) //
                                .finallyDo(OtacLine.invokeMethod(subSeqOutput, "close")) //
                                .build()) //
                        .build()) //
                .build();
    }

    private String buildMethodForIterable(PojoType type) {

        var elementType = OtacType.customType(type.getSimpleName());
        String target = "target";

        OtacCodeElement entryHandler = handleAnyType("entry", OtacValue.variable("i"), type);

        if (entryHandler == null)
            entryHandler = OtacLine.lineComment("no handle available for type " + type.getSimpleName());

        var method = generateTempMethod() //
                .parameter(OtacParameter.parameter( //
                        OtacType.explicitlyBuilder() //
                                .type(Iterable.class) //
                                .genericType(OtacGeneric.genericDeclared(type.getSimpleName())) //
                                .build(), //
                        target)) //
                .parameter(OtacParameter.parameter(PojoSequenceOutput.class, "output")) //
                .addLine(OtacLine.declare(int.class, "i", 0)) //
                .addLine(OtacForeach.builder() //
                        .type(elementType) //
                        .variableName("entry") //
                        .sequence(OtacValue.variable("target")) //
                        .addLine(entryHandler) //
                        .addLine(OtacLine.customLine("i++")) //
                        .build()) //
                .build();
        getClassBuilder().method(method);
        return method.getName();
    }

    private String buildMethodForMap(PojoType keyType, PojoType valueType) {

        var genericKey = OtacGeneric.genericDeclared(keyType.getSimpleName());
        var genericValue = OtacGeneric.genericDeclared(valueType.getSimpleName());
        var entryType = OtacType.explicitlyBuilder() //
                .type(Map.Entry.class) //
                .genericType(genericKey).genericType(genericValue) //
                .build();

        OtacCodeElement onNotnullHandler = handleAnyType("value", OtacValue.variable("key"), valueType);

        if (onNotnullHandler == null)
            onNotnullHandler = OtacLine.lineComment("no handle available for type " + valueType.getSimpleName());

        OtacValue key = OtacCastedValue.builder() //
                .target(OtacValue.methodReturn(OtacValue.variable("entry"), "getKey")) //
                .castTo(OtacType.customType(keyType.getSimpleName())) //
                .build();

        if (keyType.asSimple().type() == String.class)
            key = OtacValue.methodReturn(key, "getBytes");

        var value = OtacCastedValue.builder() //
                .target(OtacValue.methodReturn(OtacValue.variable("entry"), "getValue")) //
                .castTo(OtacType.customType(valueType.getSimpleNameWithoutGeneric())) //
                .build();

        var otacValueType = OtacType.customType(valueType.getSimpleName());
        var method = generateTempMethod() //
                .parameter( //
                        OtacParameter.parameter( //
                                OtacType.explicitlyBuilder() //
                                        .type(Map.class) //
                                        .genericType(genericKey).genericType(genericValue) //
                                        .build(),
                                "target")) //
                .parameter(OtacParameter.parameter(PojoSchemaOutput.class, "output")) //
                .addLine(OtacForeach.builder() //
                        .variableName("entry") //
                        .type(entryType) //
                        .sequence(OtacValue.methodReturn(OtacValue.variable("target"), "entrySet")) //
                        .addLine(OtacLine.declare(byte[].class, "key", key))
                        .addLine(OtacLine.declare(otacValueType, "value", value)) //
                        .addLine(onNotnullHandler) //
                        .build()) //
                .build();
        getClassBuilder().method(method);
        return method.getName();
    }

    private OtacCodeElement handleMap(String localValueName, OtacValue outputKey, PojoType keyType, PojoType valueType,
            boolean isRoot) {
        if (!keyType.isSimple()
                || (keyType.asSimple().type() != String.class && keyType.asSimple().type() != byte[].class))
            throw new PojoException("Map's key must be String or byte[], got: " + keyType.getSimpleName());

        var output = OtacValue.variable("output");
        var localValue = OtacValue.variable(localValueName);
        if (valueType.isSimple() && valueType.asSimple().type() == Object.class) {
            return OtacLine.invokeMethod(OtacValue.ofType(GenericPojoSchemaHelper.class), "serializeSchema", outputKey,
                    localValue, output);
        }

        var methodName = buildMethodForMap(keyType, valueType);

        var _output = OtacValue.variable(SCHEMA_OUTPUT_VARNAME);
        var initSubOutput = isRoot //
                ? OtacLine.assignVariable(//
                        SCHEMA_OUTPUT_VARNAME, //
                        OtacValue.methodReturn( //
                                output, //
                                "openSchema", //
                                outputKey)) //
                : OtacLine.declare( //
                        PojoSchemaOutput.class, //
                        SCHEMA_OUTPUT_VARNAME, //
                        OtacValue.methodReturn( //
                                output, //
                                "openSchema", //
                                outputKey));

        return OtacBlock.builder() //
                .wrapped(false) //
                .addLine(OtacIf.builder() //
                        .condition(OtacLine.customLine(localValueName + " == null")) //
                        .addLine(OtacLine.invokeMethod( //
                                output, //
                                "writeNull", //
                                outputKey))
                        .orElse(OtacElse.builder() //
                                .addLine(initSubOutput) //
                                .addLine(OtacTry.builder() //
                                        .addLine(OtacLine.invokeMethod( //
                                                methodName, //
                                                localValue, //
                                                _output)) //
                                        .finallyDo(OtacLine.invokeMethod(_output, "close")) //
                                        .build())
                                .build()) //
                        .build()) //
                .build();
    }

    private String buildMethodForArray(PojoType componentType) {
        var forLoop = OtacFor.builder() //
                .init(OtacLine.customLine("int j=0")) //
                .condition(OtacLine.customLine("j < target.length")) //
                .afterLoop(OtacLine.customLine("j++"));

        OtacCodeElement onNotnullHandler = handleAnyType("target[j]", OtacValue.variable("j"), componentType);

        if (onNotnullHandler == null)
            onNotnullHandler = OtacLine.lineComment("no handle available for type " + componentType.getSimpleName());

        forLoop.addLine(onNotnullHandler);

        var method = generateTempMethod() //
                .parameter(OtacParameter.parameter( //
                        OtacType.customType(componentType.getSimpleName() + "[]"), //
                        "target")) //
                .parameter(OtacParameter.parameter(PojoSequenceOutput.class, "output")) //
                .addLine(forLoop.build()) //
                .build();

        getClassBuilder().method(method);
        return method.getName();
    }

    private OtacCodeElement handleArray(String localValueName, OtacValue outputKey, PojoArrayType type,
            boolean isRoot) {

        var componentType = type.componentType();
        if (componentType.isSimple() && PojoOutput.isDirectSupported(componentType.asSimple().type())) {
            var typeName = componentType.getSimpleName();
            var outputInvokedMethodName = "write" + upperCaseFirstLetter(typeName) + "Array";
            var outputVariable = OtacValue.variable("output");

            return OtacLine.invokeMethod( //
                    outputVariable, //
                    outputInvokedMethodName, //
                    outputKey, //
                    OtacValue.variable(localValueName));
        }

        var methodName = buildMethodForArray(componentType);
        var _output = OtacValue.variable(SEQUENCE_OUTPUT_VARNAME);
        var initSubOutput = isRoot //
                ? OtacLine.assignVariable(//
                        SEQUENCE_OUTPUT_VARNAME, //
                        OtacValue.methodReturn( //
                                OtacValue.variable("output"), //
                                "openSequence", //
                                outputKey))
                : OtacLine.declare(//
                        PojoSequenceOutput.class, //
                        SEQUENCE_OUTPUT_VARNAME, //
                        OtacValue.methodReturn( //
                                OtacValue.variable("output"), //
                                "openSequence", //
                                outputKey));

        return OtacBlock.builder() //
                .wrapped(false) //
                .addLine(OtacIf.builder() //
                        .condition(OtacLine.customLine(localValueName + " == null")) //
                        .addLine(OtacLine.invokeMethod(OtacValue.variable("output"), "writeNull", outputKey)) //
                        .orElse(OtacElse.builder() //
                                .addLine(initSubOutput) //
                                .addLine(OtacTry.builder() //
                                        .addLine(OtacLine.invokeMethod( //
                                                methodName, //
                                                OtacValue.variable(localValueName), //
                                                _output)) //
                                        .finallyDo(OtacLine.invokeMethod(_output, "close")) //
                                        .build()) //
                                .build()) //
                        .build()) //
                .build();
    }
}
