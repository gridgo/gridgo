package io.gridgo.utils.pojo;

import java.util.List;

public class AbstractProxyBuilder {

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

    protected String createAllFields(List<PojoFieldSignature> methodSignatures) {
        var allFieldsBuilder = new StringBuilder();
        for (PojoFieldSignature signature : methodSignatures) {
            if (allFieldsBuilder.length() > 0) {
                allFieldsBuilder.append(",");
            }
            allFieldsBuilder.append("\"").append(signature.getFieldName()).append("\"");
        }

        return allFieldsBuilder.toString();
    }

    protected String buildSignatureFields(List<PojoFieldSignature> methodSignatures) {
        var sb = new StringBuilder();

        var subfix = "Signature";
        var type = "PojoFieldSignature";

        for (PojoFieldSignature methodSignature : methodSignatures) {
            String fieldName = methodSignature.getFieldName() + subfix;
            sb.append("private " + type + " " + fieldName + ";\n");
        }
        return sb.toString();
    }

    protected String buildSignaturesFieldAndMethod(List<PojoFieldSignature> methodSignatures) {
        var field = "private List<PojoFieldSignature> signatures = new ArrayList<>(" + methodSignatures.size() + ");";
        var method = "public List<PojoFieldSignature> getSignatures() { return this.signatures; }";
        return field + "\n\n" + method;
    }

    protected String buildGetFieldsMethod(String allFields) {
        var initValue = allFields.length() == 0 ? "new String[0];" : "new String[] {" + allFields + "};";
        var field = "private String[] fields = " + initValue;
        return field + "\n\npublic String[] getFields() { return this.fields; }";
    }

    protected String buildSetSignatureMethod(List<PojoFieldSignature> methodSignatures) {
        var type = "PojoFieldSignature";
        var subfix = "Signature";
        var method = "public void setMethodSignature(String fieldName, " + type + " value) {\n";
        for (PojoFieldSignature methodSignature : methodSignatures) {
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
