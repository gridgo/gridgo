package io.gridgo.bean.pojo;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BContainer;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.BValue;
import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.PojoMethodType;
import io.gridgo.utils.pojo.setter.data.GenericData;
import io.gridgo.utils.pojo.translator.RegisterValueTranslator;

public final class BElementTranslators {

    private BElementTranslators() {
        // Nothing to do
    }

    @RegisterValueTranslator(value = "toBArray", defaultFor = PojoMethodType.SETTER, defaultType = BArray.class)
    public static BArray toBArray(GenericData ele, PojoMethodSignature signature) {
        if (ele == null || ele.isNull())
            return null;

        if (BGenericData.class.isInstance(ele)) {
            var bElement = ((BGenericData) ele).getBElement();

            if (bElement.isArray())
                return bElement.asArray();

            throw new IllegalArgumentException("Expected for sequence data, got: " + bElement.getType());
        }

        if (ele.isSequence())
            return BArray.of(ele.asSequence().toList());

        throw new IllegalArgumentException("Expected for sequence data, got: " + ele.getClass());
    }

    @RegisterValueTranslator(value = "toBObject", defaultFor = PojoMethodType.SETTER, defaultType = BObject.class)
    public static BObject toBObject(GenericData ele, PojoMethodSignature signature) {
        if (ele == null || ele.isNull())
            return null;

        if (BGenericData.class.isInstance(ele)) {
            var bElement = ((BGenericData) ele).getBElement();

            if (bElement.isObject())
                return bElement.asObject();

            if (bElement.isReference())
                return bElement.asReference().toBObject();

            throw new IllegalArgumentException("Expected for key-value or reference data, got: " + bElement.getType());
        }

        if (ele.isKeyValue())
            return BObject.wrap(ele.asKeyValue().toMap());

        if (ele.isReference())
            return BObject.ofPojo(ele.getInnerValue());

        throw new IllegalArgumentException("Expected for key-value data, got: " + ele.getClass());
    }

    @RegisterValueTranslator(value = "toBValue", defaultFor = PojoMethodType.SETTER, defaultType = BValue.class)
    public static BValue toBValue(GenericData ele, PojoMethodSignature signature) {
        if (ele == null || ele.isNull())
            return null;

        if (BGenericData.class.isInstance(ele)) {
            var bElement = ((BGenericData) ele).getBElement();

            if (bElement.isValue())
                return bElement.asValue();

            throw new IllegalArgumentException("Expected for primitive data, got: " + bElement.getType());
        }

        if (ele.isPrimitive())
            return BValue.of(ele.asPrimitive().getData());

        throw new IllegalArgumentException("Expected for primitive data, got: " + ele.getClass());
    }

    @RegisterValueTranslator(value = "toBReference", defaultFor = PojoMethodType.SETTER, defaultType = BReference.class)
    public static BReference toBReference(GenericData ele, PojoMethodSignature signature) {
        if (ele == null || ele.isNull())
            return null;

        if (BGenericData.class.isInstance(ele)) {
            var bElement = ((BGenericData) ele).getBElement();

            if (bElement.isReference())
                return bElement.asReference();

            throw new IllegalArgumentException("Expected for reference data, got: " + bElement.getType());
        }

        if (ele.isReference())
            return BReference.of(ele.asReference().getReference());

        throw new IllegalArgumentException("Expected for reference data, got: " + ele.getClass());
    }

    @RegisterValueTranslator(value = "toBContainer", defaultFor = PojoMethodType.SETTER, defaultType = BContainer.class)
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

    @RegisterValueTranslator(value = "toBElement", defaultFor = PojoMethodType.SETTER, defaultType = BElement.class)
    public static BElement toBElement(GenericData ele, PojoMethodSignature signature) {
        if (ele == null || ele.isNull())
            return null;

        if (BGenericData.class.isInstance(ele))
            return ((BGenericData) ele).getBElement();

        return BElement.wrapAny(ele.getInnerValue());
    }
}
