package io.gridgo.pojo;

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
import io.gridgo.otac.code.block.OtacElse;
import io.gridgo.otac.code.block.OtacFor;
import io.gridgo.otac.code.block.OtacIf;
import io.gridgo.otac.code.block.OtacTry;
import io.gridgo.otac.code.line.OtacLine;
import io.gridgo.otac.value.OtacValue;
import io.gridgo.pojo.annotation.FieldTag;
import io.gridgo.pojo.output.PojoOutput;
import io.gridgo.pojo.output.PojoSchemaOutput;
import io.gridgo.pojo.output.PojoSequenceOutput;
import io.gridgo.pojo.reflect.PojoReflectiveGetter;
import io.gridgo.pojo.reflect.PojoReflectiveSetter;
import io.gridgo.pojo.reflect.PojoReflectiveStruct;
import io.gridgo.pojo.translator.PojoUnboxer;
import io.gridgo.pojo.translator.PojoUnboxerRegistry;
import io.gridgo.utils.StringUtils;
import io.gridgo.utils.pojo.exception.PojoException;
import lombok.NonNull;

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
            var fieldType = getter.fieldType();
            var fieldNameKey = "_" + fieldName + "_key";

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

        var outputVariable = OtacValue.variable("output");
        var castedTarget = OtacValue.variable(Consts.castedTargetVarname);

        var loopBuilder = OtacFor.builder() //
                .init(OtacLine.declare(int.class, "i", 0)) //
                .condition(OtacLine.customLine("i<" + fieldName + ".length")) //
                .afterLoop(OtacLine.customLine("i++"));

        var loopEntryName = "entry";
        var loopEntryVariable = OtacValue.variable(loopEntryName);

        var sequenceOutputVariable = OtacValue.variable(Consts.sequenceOutputVarname);
        if (fieldType.isArray()) {
            var componentType = fieldType.getComponentType();
            loopBuilder
                    .addLine(OtacLine.declare(componentType, loopEntryName, OtacValue.customValue(fieldName + "[i]")));

            var unboxer = PojoUnboxerRegistry.getInstance().lookup(componentType);
            if (unboxer != null) {
                classBuilder.require(unboxer.declaringClass());

                var unboxerReturnType = unboxer.returnType();
                var typeName = unboxerReturnType.isArray() ? unboxerReturnType.getComponentType().getSimpleName()
                        : unboxerReturnType.getSimpleName();
                var outputInvokedMethodName = "write" + StringUtils.upperCaseFirstLetter(typeName);
                if (unboxerReturnType.isArray())
                    outputInvokedMethodName += "Array";

                loopBuilder.addLine( //
                        OtacIf.builder() //
                                .condition(OtacValue.customValue(loopEntryName + " == null")) //
                                .addLine(OtacLine
                                        .invokeMethod(sequenceOutputVariable, "writeNull", OtacValue.variable("i")))
                                .orElse(OtacElse.builder() //
                                        .addLine(OtacLine.invokeMethod( //
                                                sequenceOutputVariable, //
                                                outputInvokedMethodName, //
                                                OtacValue.variable("i"), //
                                                OtacValue.methodReturn( //
                                                        OtacValue.ofType(unboxer.declaringClass()), //
                                                        unboxer.methodName(), //
                                                        loopEntryVariable))) //
                                        .build())
                                .build());
            }
        }

        var elseBlockBuilder = OtacElse.builder() //
                .addLine(OtacLine.assignVariable(Consts.sequenceOutputVarname,
                        OtacValue.methodReturn(outputVariable, "openSequence", OtacValue.field(fieldNameKey)))) //
                .addLine(OtacTry.builder() //
                        .addLine(loopBuilder.build()) //
                        .finallyDo(OtacLine.invokeMethod(sequenceOutputVariable, "close")) //
                        .build());

        serializeMethodBuilder //
                .addLine(OtacLine.declare( //
                        OtacType.typeOf(fieldType), //
                        fieldName, //
                        OtacValue.methodReturn(castedTarget, getter.element().name()))) //
                .addLine(OtacIf.builder() //
                        .condition(OtacValue.customValue(fieldName + " == null")) //
                        .addLine(OtacLine.invokeMethod(outputVariable, "writeNull", OtacValue.variable(fieldNameKey)))
                        .orElse(elseBlockBuilder.build()) //
                        .build());
    }

    private void handlePrimitiveField(PojoReflectiveGetter getter, String fieldNameKey) {
        var fieldType = getter.fieldType();
        var typeName = fieldType.isArray() ? fieldType.getComponentType().getSimpleName() : fieldType.getSimpleName();
        var outputInvokedMethodName = "write" + StringUtils.upperCaseFirstLetter(typeName);
        if (fieldType.isArray())
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
        var fieldType = getter.fieldType();
        serializeMethodBuilder //
                .addLine(OtacLine.declare(OtacType.typeOf(fieldType), fieldName,
                        OtacValue.methodReturn(castedTarget, getter.element().name()))) //
                .addLine(OtacIf.builder() //
                        .condition(OtacValue.customValue(fieldName + " == null")) //
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
        var schemaFieldName = "_" + StringUtils.lowerCaseFirstLetter(fieldName) + "_schema";

        classBuilder.field(OtacField.builder() //
                .name(schemaFieldName) //
                .accessLevel(OtacAccessLevel.PRIVATE) //
                .type(OtacType.typeOf(PojoSchema.class)) //
                .build());

        classBuilder //
                .require(PojoSchemaBuilder.class) //
                .require(fieldType) //
                .require(PojoSchemaConfig.class);

        initMethodBuilder.addLine(OtacLine.assignField(schemaFieldName,
                OtacValue.customValue("new " + PojoSchemaBuilder.class.getSimpleName() + "(" + fieldType.getSimpleName()
                        + ".class, " + PojoSchemaConfig.class.getSimpleName() + ".DEFAULT).build()")));

        serializeMethodBuilder //
                .addLine(OtacLine.declare( //
                        OtacType.typeOf(fieldType), //
                        fieldName, //
                        OtacValue.methodReturn(castedTarget, getter.element().name()))) //
                .addLine(OtacIf.builder() //
                        .condition(OtacValue.customValue(fieldName + " == null")) //
                        .addLine(OtacLine
                                .invokeMethod(outputVariable, "writeNull", OtacValue.field(fieldNameKey)))
                        .orElse(OtacElse.builder() //
                                .addLine(OtacLine.assignVariable( //
                                        Consts.schemaOutputVarname, //
                                        OtacValue.methodReturn( //
                                                outputVariable, //
                                                "openSchema", //
                                                OtacValue.field(fieldNameKey)))) //
                                .addLine(OtacTry.builder() //
                                        .addLine( //
                                                OtacLine.invokeMethod( //
                                                        OtacValue.field(schemaFieldName), //
                                                        "serialize", //
                                                        OtacValue.variable(fieldName), //
                                                        OtacValue.variable(Consts.schemaOutputVarname)))
                                        .finallyDo( //
                                                OtacLine.invokeMethod( //
                                                        OtacValue.variable(Consts.schemaOutputVarname), //
                                                        "close")) //
                                        .build()) //
                                .build())
                        .build()) //
        ;
    }
}
