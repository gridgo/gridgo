package io.gridgo.bean.pojo;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BContainer;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.BValue;
import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.setter.data.GenericData;
import io.gridgo.utils.pojo.translator.RegisterValueTranslator;

public abstract class BElementTranslators {

    @RegisterValueTranslator("toBArray")
    public static BArray toBArray(GenericData ele, PojoMethodSignature signature) {
        if (ele == null)
            return null;

        if (BGenericData.class.isInstance(ele)) {
            var bElement = ((BGenericData) ele).getBElement();

            if (bElement.isNullValue())
                return null;

            return bElement.asArray();
        }

        return BArray.of(ele.asSequence().toList());
    }

    @RegisterValueTranslator("toBObject")
    public static BObject toBObject(GenericData ele, PojoMethodSignature signature) {
        if (ele == null)
            return null;

        if (BGenericData.class.isInstance(ele)) {
            var bElement = ((BGenericData) ele).getBElement();

            if (bElement.isNullValue())
                return null;

            if (bElement.isObject())
                return bElement.asObject();

            if (bElement.isReference())
                return bElement.asReference().toBObject();

            throw new IllegalArgumentException("Expected for key-value or reference data, got: " + bElement.getType());
        }

        return BObject.wrap(ele.asKeyValue().toMap());
    }

    @RegisterValueTranslator("toBValue")
    public static BValue toBValue(GenericData ele, PojoMethodSignature signature) {
        if (ele == null)
            return null;
        if (BGenericData.class.isInstance(ele)) {
            var bElement = ((BGenericData) ele).getBElement();

            if (bElement.isNullValue())
                return null;

            return bElement.asValue();
        }

        return BValue.of(ele.asPrimitive().getData());
    }

    @RegisterValueTranslator("toBReference")
    public static BReference toBReference(GenericData ele, PojoMethodSignature signature) {
        if (ele == null)
            return null;

        if (BGenericData.class.isInstance(ele)) {
            var bElement = ((BGenericData) ele).getBElement();

            if (bElement.isNullValue())
                return null;

            return bElement.asReference();
        }

        return BReference.of(ele.asReference().getReference());
    }

    @RegisterValueTranslator("toBContainer")
    public static BContainer toBContainer(GenericData ele, PojoMethodSignature signature) {
        if (ele == null || ele.isNull())
            return null;

        if (ele.isKeyValue())
            return toBObject(ele, signature);

        if (ele.isSequence())
            return toBArray(ele, signature);

        if (ele.isReference())
            return BObject.ofPojo(ele.asReference().getReference());

        throw new IllegalArgumentException("Expect key-value or sequence or reference data, got " + ele);
    }

    @RegisterValueTranslator("toBElement")
    public static BElement toBElement(GenericData ele, PojoMethodSignature signature) {
        if (BGenericData.class.isInstance(ele))
            return ((BGenericData) ele).getBElement();

        return BElement.wrapAny(ele.getInnerValue());
    }
}
