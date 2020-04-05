package io.gridgo.utils.pojo;

import static io.gridgo.otac.OtacAccessLevel.PRIVATE;
import static io.gridgo.otac.OtacType.typeOf;

import java.util.List;
import java.util.stream.Collectors;

import io.gridgo.otac.OtacField;

public class AbstractProxyBuilder {

    protected static final String SIGNATURE_FIELD_SUBFIX = "Signature";

    protected String addTabToAllLine(int numTab, String origin) {
        var tabs = "";
        for (int i = 0; i < numTab; i++)
            tabs += "    ";

        var lines = origin.split("\n");
        var sb = new StringBuilder();
        for (var line : lines) {
            sb.append(tabs).append(line).append("\n");
        }
        return sb.toString();
    }

    protected void doImport(StringBuilder classContent, Class<?>... types) {
        for (var typeToImport : types)
            classContent.append("import " + typeToImport.getName() + ";\n");
    }

    protected String createAllFields(List<PojoMethodSignature> methodSignatures) {
        var allFieldsBuilder = new StringBuilder();
        for (PojoMethodSignature signature : methodSignatures) {
            if (allFieldsBuilder.length() > 0) {
                allFieldsBuilder.append(",");
            }
            allFieldsBuilder.append("\"").append(signature.getFieldName()).append("\"");
        }

        return allFieldsBuilder.toString();
    }

    protected OtacField signatureToField(PojoMethodSignature signature) {
        return OtacField.builder() //
                .accessLevel(PRIVATE) //
                .type(typeOf(PojoMethodSignature.class)) //
                .name(signature.getFieldName() + "Signature") //
                .build();
    }

    protected List<OtacField> buildSignatureFields(List<PojoMethodSignature> methodSignatures) {
        return methodSignatures.stream() //
                .map(this::signatureToField) //
                .collect(Collectors.toList());
    }

    protected String buildSignaturesFieldAndMethod(List<PojoMethodSignature> methodSignatures) {
        var field = "private List<PojoMethodSignature> signatures = new ArrayList<>(" + methodSignatures.size() + ");";
        var method = "public List<PojoMethodSignature> getSignatures() { return this.signatures; }";
        return field + "\n\n" + method;
    }

    protected String buildGetFieldsMethod(String allFields) {
        var initValue = allFields.length() == 0 ? "new String[0];" : "new String[] {" + allFields + "};";
        var field = "private String[] fields = " + initValue;
        return field + "\n\npublic String[] getFields() { return this.fields; }";
    }

    protected String buildSetSignatureMethod(List<PojoMethodSignature> methodSignatures) {
        var type = "PojoMethodSignature";
        var subfix = "Signature";
        var method = "public void setMethodSignature(String fieldName, " + type + " value) {\n";
        for (PojoMethodSignature methodSignature : methodSignatures) {
            var fieldName = methodSignature.getFieldName();
            var signFieldName = fieldName + subfix;
            method += "\tif (\"" + fieldName + "\".equals(fieldName)) {\n";
            method += "\t\t" + signFieldName + " = value;\n";
            method += "\t\tsignatures.add(value);\n";
            method += "\t\treturn;\n";
            method += "\t}\n";
        }
        method += "}"; // end of method

        return method;
    }

}
