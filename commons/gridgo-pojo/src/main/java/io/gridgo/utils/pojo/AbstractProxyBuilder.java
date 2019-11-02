package io.gridgo.utils.pojo;

import java.util.List;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

public class AbstractProxyBuilder {

    protected void buildSignatureMethod(CtClass cc, List<PojoMethodSignature> methodSignatures) throws CannotCompileException {
        String type = "io.gridgo.utils.pojo.PojoMethodSignature";
        String subfix = "Signature";
        for (PojoMethodSignature methodSignature : methodSignatures) {
            String fieldName = methodSignature.getFieldName() + subfix;
            cc.addField(CtField.make("private " + type + " " + fieldName + ";", cc));
        }

        String method = "public void setMethodSignature(String fieldName, " + type + " value) {\n";
        method += "\tfor (int i=0; i<this.fields.length; i++) {"; // start for loop via all field
        for (PojoMethodSignature methodSignature : methodSignatures) {
            String fieldName = methodSignature.getFieldName();
            String signFieldName = fieldName + subfix;
            method += "\t\tif (\"" + fieldName + "\".equals(fieldName)) {\n";
            method += "\t\t\t" + signFieldName + " = value;\n";
            method += "\t\t\tthis.signatures.add(value); \n";
            method += "\t\t}\n";
        }
        method += "\t}\n"; // end of for
        method += "}"; // end of method

        cc.addMethod(CtMethod.make(method, cc));
    }

    protected void buildGetFieldsMethod(CtClass cc, String allFields) throws CannotCompileException {
        String initValue = allFields.length() == 0 ? "new String[0];" : "new String[] {" + allFields + "};";
        CtField field = CtField.make("private String[] fields = " + initValue, cc);
        cc.addField(field);

        String method = "public String[] getFields() { return this.fields; }";
        cc.addMethod(CtMethod.make(method, cc));
    }

    protected void buildGetSignaturesMethod(CtClass cc) throws CannotCompileException {
        CtField field = CtField.make("private java.util.List signatures = new java.util.ArrayList();", cc);
        cc.addField(field);

        String method = "public java.util.List getSignatures() { return this.signatures; }";
        cc.addMethod(CtMethod.make(method, cc));
    }
}
