package io.gridgo.otac;

import static io.gridgo.otac.OtacAccessLevel.PUBLIC;
import static io.gridgo.otac.OtacParameter.parameter;
import static io.gridgo.otac.OtacType.VOID;
import static io.gridgo.otac.value.OtacValue.field;
import static io.gridgo.otac.value.OtacValue.variable;
import static io.gridgo.otac.code.line.OtacLine.assignField;
import static io.gridgo.otac.code.line.OtacLine.returnValue;
import static io.gridgo.otac.utils.OtacUtils.tabs;
import static io.gridgo.utils.StringUtils.upperCaseFirstLetter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class OtacClass extends OtacModifiers implements OtacRequireImports {

    @Getter
    @Singular("annotatedBy")
    private List<OtacAnnotation> annotations;

    @Getter
    private String packageName;

    @Getter
    private String simpleClassName;

    @Getter
    private OtacType extendsFrom;

    @Getter
    @Singular("implement")
    private final List<OtacType> interfaces;

    @Getter
    @Singular
    private final List<OtacGeneric> generics;

    @Getter
    @Singular
    private final List<OtacField> fields;

    @Getter
    @Singular
    private final List<OtacConstructor> constructors;

    @Getter
    @Singular
    private final List<OtacMethod> methods;

    @Getter
    @Singular("require")
    private Set<Class<?>> imports;

    public String getName() {
        return (packageName == null || packageName.isBlank() ? "" : (packageName + ".")) + simpleClassName;
    }

    @Override
    public Set<Class<?>> requiredImports() {
        var imports = new HashSet<Class<?>>();
        if (this.imports != null)
            imports.addAll(this.imports);
        if (!getAnnotations().isEmpty())
            for (var a : getAnnotations())
                imports.addAll(a.requiredImports());
        if (interfaces != null)
            for (var i : interfaces)
                imports.addAll(i.requiredImports());
        if (extendsFrom != null)
            imports.addAll(extendsFrom.requiredImports());
        if (methods != null)
            for (var m : methods)
                imports.addAll(m.requiredImports());
        if (fields != null)
            for (var f : fields)
                imports.addAll(f.requiredImports());
        if (constructors != null)
            for (var c : constructors)
                imports.addAll(c.requiredImports());
        if (generics != null)
            for (var g : generics)
                imports.addAll(g.requiredImports());
        return imports;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        makePackage(sb);
        sb.append("\n");
        makeImports(sb);
        appendAnnotations(sb);
        makeClassName(sb);
        sb.append("{\n\n");
        appendFields(sb);
        appendConstructor(sb);
        appendMethods(sb);
        sb.append("}");
        return sb.toString();
    }

    private void appendAnnotations(StringBuilder sb) {
        for (var a : getAnnotations())
            sb.append(a.toString()).append("\n");
    }

    private void appendConstructor(StringBuilder sb) {
        for (var ctor : constructors) {
            ctor.setDeclaringClass(this);
            sb.append(tabs(1, ctor.toString())).append("\n\n");
        }
    }

    private void appendMethods(StringBuilder sb) {
        var methods = new HashSet<OtacMethod>();
        if (methods != null)
            methods.addAll(this.methods);

        if (fields != null) {
            for (var f : fields) {
                var fName = f.getName();
                if (f.isGenerateGetter()) {
                    var prefix = f.getType().getType() == boolean.class ? "is" : "get";
                    var getterName = prefix + upperCaseFirstLetter(fName);
                    methods.add(OtacMethod.builder()//
                            .accessLevel(PUBLIC) //
                            .returnType(f.getType()) //
                            .name(getterName) //
                            .addLine(returnValue(field(fName))) //
                            .build());
                }
                if (f.isGenerateSetter() && !f.isFinal()) {
                    var setterName = "set" + upperCaseFirstLetter(fName);
                    methods.add(OtacMethod.builder()//
                            .accessLevel(PUBLIC) //
                            .returnType(VOID) //
                            .parameter(parameter(f.getType(), fName)) //
                            .name(setterName) //
                            .addLine(assignField(fName, variable(fName)))//
                            .build());
                }
            }
        }

        if (methods.size() == 0)
            return;

        for (var m : methods) {
            m.setDeclaringClass(this);
            sb.append(tabs(1, m.toString())).append("\n\n");
        }

    }

    private void appendFields(StringBuilder sb) {
        if (fields == null || fields.isEmpty())
            return;

        for (var f : fields)
            f.setDeclaringClass(this);

        sb.append(tabs(1, fields.get(0).toString()));
        for (int i = 1; i < fields.size(); i++)
            sb.append("\n").append(tabs(1, fields.get(i).toString()));

        sb.append("\n\n");
    }

    private void makeClassName(StringBuilder sb) {
        sb //
                .append(getAccessLevel().getKeyword()) //
                .append(isStatic() ? "static " : "") //
                .append(isFinal() ? "final " : "") //
                .append(isAbstract() ? "abstract " : "") //
                .append("class ") //
                .append(getSimpleClassName());
        if (generics != null && generics.size() > 0) {
            sb.append("<");
            sb.append(generics.get(0).toString().trim());
            for (int i = 1; i < generics.size(); i++) {
                sb.append(", ").append(generics.get(i).toString().trim());
            }
            sb.append(">");
        }
        sb.append(" ");
        if (extendsFrom != null)
            sb.append("extends ").append(extendsFrom.toString());

        if (interfaces.size() > 0) {
            sb.append("implements ") //
                    .append(interfaces.get(0).toString().trim());
            for (int i = 1; i < interfaces.size(); i++)
                sb.append(", ").append(interfaces.get(i).toString().trim());

            sb.append(" ");
        }
    }

    private void makeImports(StringBuilder sb) {
        var requiredImports = requiredImports();
        if (requiredImports.isEmpty())
            return;
        for (var i : requiredImports) {
            if (i.isArray())
                i = i.getComponentType();
            var name = i.getName();
            if (name.startsWith("java.lang.") || i.isPrimitive())
                continue;
            sb.append("import ").append(name).append(";\n");
        }
        sb.append("\n");
    }

    private void makePackage(StringBuilder sb) {
        if (packageName != null && !packageName.isBlank())
            sb.append("package " + packageName + ";\n");
    }

    public String printWithLineNumber() {
        var code = this.toString();
        var lines = code.split("\n");
        var sb = new StringBuilder();
        var lineNumber = 1;
        for (var line : lines)
            sb.append(lineNumber++).append("\t").append(line).append("\n");
        return sb.toString();
    }
}
