package io.gridgo.pojo.builder;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import io.gridgo.otac.code.block.OtacBlock.OtacBlockBuilder;
import io.gridgo.otac.code.block.OtacElse;
import io.gridgo.otac.code.block.OtacForeach;
import io.gridgo.otac.code.block.OtacForeach.OtacForeachBuilder;
import io.gridgo.otac.code.block.OtacIf;
import io.gridgo.otac.code.block.OtacTry;
import io.gridgo.otac.code.line.OtacLine;
import io.gridgo.otac.value.OtacValue;
import io.gridgo.pojo.PojoSchema;
import io.gridgo.pojo.PojoSchemaConfig;
import io.gridgo.pojo.annotation.FieldTag;
import io.gridgo.pojo.builder.template.InitExternalSchemaTemplate;
import io.gridgo.pojo.builder.template.WriteNullOrUnbox;
import io.gridgo.pojo.output.PojoOutput;
import io.gridgo.pojo.output.PojoSchemaOutput;
import io.gridgo.pojo.output.PojoSequenceOutput;
import io.gridgo.pojo.reflect.PojoReflectiveGetter;
import io.gridgo.pojo.reflect.PojoReflectiveSetter;
import io.gridgo.pojo.reflect.PojoReflectiveStruct;
import io.gridgo.pojo.reflect.type.PojoParameterizedType;
import io.gridgo.pojo.reflect.type.PojoType;
import io.gridgo.pojo.reflect.type.PojoTypes;
import io.gridgo.pojo.translator.PojoUnboxer;
import io.gridgo.pojo.translator.PojoUnboxerRegistry;
import io.gridgo.utils.StringUtils;
import io.gridgo.utils.pojo.exception.PojoException;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class PojoSchemaBuilder<T> {

    private static class Consts {
        static final String castedTargetVarname = "castedTarget";
        static final String schemaOutputVarname = "schemaOutput";
        static final String sequenceOutputVarname = "sequenceOutput";
    }

    private final @NonNull Class<T> type;

    private final @NonNull PojoSchemaConfig config;

    private final OtacClassBuilder<?, ?> classBuilder;

    private final OtacConstructorBuilder<?, ?> constructorBuilder;

    private final OtacMethodBuilder<?, ?> serializeMethodBuilder;

    private final OtacMethodBuilder<?, ?> initMethodBuilder;

    public PojoSchemaBuilder(Class<T> type, PojoSchemaConfig config) {
        this.type = type;
        this.config = config;

        classBuilder = OtacClass.builder() //
                .accessLevel(OtacAccessLevel.PUBLIC) //
                .packageName(type.getPackageName()) //
                .simpleClassName(type.getSimpleName() + "_generatedSchema___") //
                .extendsFrom(OtacType.builder() //
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
                .parameter(OtacParameter.parameter(OtacType.OBJECT, "target")) //
                .parameter(OtacParameter.parameter(PojoSchemaOutput.class, "output")) //
                .addLine(OtacLine.declare(type, Consts.castedTargetVarname, OtacValue.castVariable("target", type))) //
                .addLine(OtacLine.declare( //
                        OtacType.typeOf(PojoSchemaOutput.class), //
                        Consts.schemaOutputVarname, //
                        OtacValue.NULL)) //
                .addLine(OtacLine.declare( //
                        OtacType.typeOf(PojoSequenceOutput.class), //
                        Consts.sequenceOutputVarname, //
                        OtacValue.NULL));

        initMethodBuilder = OtacMethod.builder() //
                .accessLevel(OtacAccessLevel.PROTECTED)//
                .name("init");
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
            System.out.println(otacClass.printWithLineNumber());
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
            var fieldType = getter.fieldType().rawType();
            var fieldNameKey = PojoSchemaConvension.genFieldNameKey(fieldName);

            classBuilder.field(OtacField.builder() //
                    .accessLevel(OtacAccessLevel.PRIVATE) //
                    .isFinal(true) //
                    .type(otacByteArrayType) //
                    .name(fieldNameKey) //
                    .build());

            constructorBuilder.addLine( //
                    OtacLine.assignField( //
                            fieldNameKey, //
                            OtacValue.methodReturn( //
                                    OtacValue.raw(fieldName), //
                                    "getBytes")));

            if (PojoOutput.isDirectSupported(fieldType)) {
                handlePrimitiveField(getter, fieldNameKey);
            } else {
                var unboxer = PojoUnboxerRegistry.getInstance().lookup(fieldType);
                if (unboxer != null) {
                    handleUnboxableField(getter, fieldNameKey, unboxer);
                } else if (Collection.class.isAssignableFrom(fieldType) || fieldType.isArray()) {
                    handleSequenceField(getter, fieldNameKey);
                } else if (Map.class.isAssignableFrom(fieldType)) {
                    // deal with map
                } else {
                    handleOtherField(getter, fieldNameKey);
                }
            }
        }
    }

    private void handleSequenceField(PojoReflectiveGetter getter, String fieldNameKey) {

        var fieldName = getter.fieldName();
        var fieldType = getter.fieldType();

        var loopBuilder = OtacForeach.builder() //
                .variableName("entry") //
                .sequence(OtacValue.variable(fieldName));

        if (fieldType.rawType().isArray()) {
            handleArray(fieldName, fieldType.rawType(), loopBuilder);
        } else {
            handleCollection(getter, loopBuilder);
        }

        var outputVariable = OtacValue.variable("output");
        var castedTarget = OtacValue.variable(Consts.castedTargetVarname);

        serializeMethodBuilder //
                .addLine(OtacLine.declare( //
                        OtacType.typeOf(fieldType.rawType()), //
                        fieldName, //
                        OtacValue.methodReturn(castedTarget, getter.element().name()))) //
                .addLine(OtacIf.builder() //
                        .condition(OtacLine.customLine(fieldName + " == null")) //
                        .addLine(OtacLine.invokeMethod(outputVariable, "writeNull", OtacValue.variable(fieldNameKey)))
                        .orElse(OtacElse.builder() //
                                .addLine(OtacLine.assignVariable(Consts.sequenceOutputVarname,
                                        OtacValue.methodReturn(outputVariable, "openSequence",
                                                OtacValue.field(fieldNameKey)))) //
                                .addLine(OtacTry.builder() //
                                        .addLine(OtacLine.declare(int.class, "i", 0)) // declare index var
                                        .addLine(loopBuilder //
                                                .addLine(OtacLine.customLine("i++")) // increase index var
                                                .build()) //
                                        .finallyDo(OtacLine.invokeMethod(
                                                OtacValue.variable(Consts.sequenceOutputVarname), "close")) //
                                        .build()) //
                                .build()) //
                        .build());
    }

    private void handleCollection(PojoReflectiveGetter getter, OtacForeachBuilder<?, ?> loopBuilder) {
        var element = getter.element();
        var typeInfo = PojoTypes.extractTypeInfo(element.method().getGenericReturnType(), element.effectiveClass());
        if (!Collection.class.isAssignableFrom(typeInfo.rawType()))
            throw new PojoException("illegal setter return type: " + getter.fieldType());

        var elementType = ((PojoParameterizedType) typeInfo).actualTypeArguments().get(0);
        loopBuilder.type(OtacType.typeOf(elementType.rawType()));
        buildLoopForGeneric(getter.fieldName(), elementType, loopBuilder);
    }

    private void buildLoopForGeneric(String fieldName, PojoType type, OtacBlockBuilder<?, ?> loopBuilder) {
        if (type instanceof PojoParameterizedType) {

        } else {
            System.out.println("process non-parameterized type: " + type);
            var unboxer = PojoUnboxerRegistry.getInstance().lookup(type.rawType());
            if (unboxer != null) {
                
            } else {
                var schemaFieldName = InitExternalSchemaTemplate.builder() //
                        .builder(this) //
                        .fieldName(fieldName) //
                        .fieldType(type.rawType()) //
                        .build() //
                        .apply();

                loopBuilder.addLine(OtacIf.builder() //
                        .condition(OtacLine.customLine("entry == null")) //
                        .addLine(OtacLine.invokeMethod( //
                                OtacValue.variable(Consts.sequenceOutputVarname), //
                                "writeNull", //
                                OtacValue.variable("i")))
                        .build());
            }
        }
    }

    private void handleArray(@NonNull String fieldName, Class<?> fieldType, OtacForeachBuilder<?, ?> loopBuilder) {
        var loopEntryName = "entry";
        var sequenceOutputVariable = OtacValue.variable(Consts.sequenceOutputVarname);

        var componentType = fieldType.getComponentType();
        loopBuilder.type(OtacType.typeOf(componentType));

        var unboxer = PojoUnboxerRegistry.getInstance().lookup(componentType);
        if (unboxer != null) {
            loopBuilder.addLine(WriteNullOrUnbox.builder() //
                    .output(Consts.sequenceOutputVarname) //
                    .outputKey("i") //
                    .unboxer(unboxer) //
                    .ifIsNull(loopEntryName) //
                    .unboxedVarname(loopEntryName) //
                    .build() //
                    .apply());
        } else {
            var schemaFieldName = InitExternalSchemaTemplate.builder() //
                    .builder(this) //
                    .fieldName(fieldName) //
                    .fieldType(componentType) //
                    .build().apply();

            var elseBuilder = OtacElse.builder() //
                    .addLine(OtacLine.assignVariable(Consts.schemaOutputVarname, //
                            OtacValue.methodReturn( //
                                    OtacValue.variable(Consts.sequenceOutputVarname), //
                                    "openSchema", //
                                    OtacValue.variable("i")))) //
                    .addLine(OtacTry.builder() //
                            .addLine(OtacLine.invokeMethod( //
                                    OtacValue.field(schemaFieldName), //
                                    "serialize", //
                                    OtacValue.variable("entry"), //
                                    OtacValue.variable(Consts.schemaOutputVarname))) //
                            .finallyDo(OtacLine.invokeMethod( //
                                    OtacValue.variable(Consts.schemaOutputVarname), //
                                    "close")) //
                            .build());

            loopBuilder.addLine(OtacIf.builder() //
                    .condition(OtacLine.customLine(loopEntryName + " == null")) //
                    .addLine(OtacLine.invokeMethod(sequenceOutputVariable, "writeNull", OtacValue.variable("i")))
                    .orElse(elseBuilder.build()) //
                    .build());
        }

    }

    private void handlePrimitiveField(PojoReflectiveGetter getter, String fieldNameKey) {
        var fieldType = getter.fieldType();
        var typeName = fieldType.rawType().isArray() ? fieldType.rawType().getComponentType().getSimpleName() : fieldType.rawType().getSimpleName();
        var outputInvokedMethodName = "write" + StringUtils.upperCaseFirstLetter(typeName);
        if (fieldType.rawType().isArray())
            outputInvokedMethodName += "Array";

        var outputVariable = OtacValue.variable("output");
        var castedTarget = OtacValue.variable(Consts.castedTargetVarname);
        serializeMethodBuilder.addLine( //
                OtacLine.invokeMethod( //
                        outputVariable, //
                        outputInvokedMethodName, //
                        OtacValue.field(fieldNameKey), //
                        OtacValue.methodReturn(castedTarget, getter.element().name())));
    }

    private void handleUnboxableField(PojoReflectiveGetter getter, String fieldNameKey, PojoUnboxer unboxer) {

        var unboxerReturnType = unboxer.returnType();
        var typeName = unboxerReturnType.isArray() ? unboxerReturnType.getComponentType().getSimpleName()
                : unboxerReturnType.getSimpleName();
        var outputInvokedMethodName = "write" + StringUtils.upperCaseFirstLetter(typeName);
        if (unboxerReturnType.isArray())
            outputInvokedMethodName += "Array";

        var outputVariable = OtacValue.variable("output");
        var castedTarget = OtacValue.variable(Consts.castedTargetVarname);

        var fieldName = getter.fieldName();
        var fieldType = getter.fieldType().rawType();
        serializeMethodBuilder //
                .addLine(OtacLine.declare(OtacType.typeOf(fieldType), fieldName,
                        OtacValue.methodReturn(castedTarget, getter.element().name()))) //
                .addLine(OtacIf.builder() //
                        .condition(OtacLine.customLine(fieldName + " == null")) //
                        .addLine(OtacLine.invokeMethod(outputVariable, "writeNull", OtacValue.field(fieldNameKey)))
                        .orElse(OtacElse.builder() //
                                .addLine(OtacLine.invokeMethod( //
                                        OtacValue.variable("output"), //
                                        outputInvokedMethodName, //
                                        OtacValue.field(fieldNameKey), //
                                        OtacValue.methodReturn( //
                                                OtacValue.ofType(unboxer.declaringClass()), //
                                                unboxer.methodName(), //
                                                OtacValue.variable(fieldName)))) //
                                .build())
                        .build()) //
        ;

    }

    private void handleOtherField(PojoReflectiveGetter getter, String fieldNameKey) {
        var fieldName = getter.fieldName();
        var fieldType = getter.fieldType();
        var outputVariable = OtacValue.variable("output");
        var castedTarget = OtacValue.variable(Consts.castedTargetVarname);
        var schemaFieldName = InitExternalSchemaTemplate.builder() //
                .builder(this) //
                .fieldName(fieldName) //
                .fieldType(fieldType.rawType()) //
                .build().apply();

        serializeMethodBuilder //
                .addLine(OtacLine.declare( // declare a local variable with the same fieldName
                        OtacType.typeOf(fieldType.rawType()), //
                        fieldName, //
                        OtacValue.methodReturn(castedTarget, getter.element().name()))) //
                .addLine(OtacIf.builder() // if the value is null, write null
                        .condition(OtacLine.customLine(fieldName + " == null")) //
                        .addLine(OtacLine
                                .invokeMethod(outputVariable, "writeNull", OtacValue.field(fieldNameKey)))
                        .orElse(OtacElse.builder() // or else, try to open new schema output
                                .addLine(OtacLine.assignVariable( //
                                        Consts.schemaOutputVarname, //
                                        OtacValue.methodReturn( //
                                                outputVariable, //
                                                "openSchema", //
                                                OtacValue.field(fieldNameKey)))) //
                                .addLine(OtacTry.builder() // try
                                        .addLine( //
                                                OtacLine.invokeMethod( // invoke "serialize" on the opened schema output
                                                                       // above
                                                        OtacValue.field(schemaFieldName), //
                                                        "serialize", //
                                                        OtacValue.variable(fieldName), //
                                                        OtacValue.variable(Consts.schemaOutputVarname)))
                                        .finallyDo( // finally
                                                OtacLine.invokeMethod( //
                                                        OtacValue.variable(Consts.schemaOutputVarname), //
                                                        "close")) //
                                        .build()) //
                                .build())
                        .build()) //
        ;
    }
}
