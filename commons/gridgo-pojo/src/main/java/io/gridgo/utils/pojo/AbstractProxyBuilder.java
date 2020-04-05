package io.gridgo.utils.pojo;

import static io.gridgo.otac.OtacAccessLevel.PRIVATE;
import static io.gridgo.otac.OtacType.typeOf;

import java.util.List;
import java.util.stream.Collectors;

import io.gridgo.otac.OtacField;

public class AbstractProxyBuilder {

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

}
