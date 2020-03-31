package io.gridgo.pojo.otac;

import static io.gridgo.pojo.otac.OtacAccessLevel.PUBLIC;
import static io.gridgo.pojo.otac.OtacType.VOID;
import static io.gridgo.pojo.otac.OtacUtils.tabs;
import static io.gridgo.utils.StringUtils.upperCaseFirstLetter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacClass extends OtacModifiers implements OtacRequireImports {

    private String packageName;

    private String simpleClassName;

    private OtacType extendsFrom;

    @Singular("implement")
    private final List<OtacType> interfaces;

    @Singular
    private final List<OtacGeneric> generics;

    @Singular
    private final List<OtacField> fields;

    @Singular
    private final List<OtacMethod> methods;

    @Singular("require")
    private final Set<Class<?>> imports = new HashSet<>();

    @Override
    public Set<Class<?>> requiredImports() {
        var imports = new HashSet<Class<?>>();
        if (this.imports != null)
            imports.addAll(this.imports);
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
        makeClassName(sb);
        sb.append("{\n");
        appendFields(sb);
        appendMethods(sb);
        sb.append("}");
        return sb.toString();
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
                            .body("return this." + fName + ";") //
                            .build());
                }
                if (f.isGenerateSetter() && !f.isFinal()) {
                    var setterName = "set" + upperCaseFirstLetter(fName);
                    methods.add(OtacMethod.builder()//
                            .accessLevel(PUBLIC) //
                            .returnType(VOID) //
                            .parameter(OtacParameter.of(fName, f.getType())) //
                            .name(setterName) //
                            .body("this." + fName + " = " + fName + ";") //
                            .build());
                }
            }
        }

        for (var m : methods)
            sb.append(tabs(1, m.toString())).append("\n");

    }

    private void appendFields(StringBuilder sb) {
        sb.append("\n");
        if (fields == null)
            return;
        for (var f : fields)
            sb.append(tabs(1)).append(f.toString());
        sb.append("\n");
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
