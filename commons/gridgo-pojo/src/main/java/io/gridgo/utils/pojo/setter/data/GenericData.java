package io.gridgo.utils.pojo.setter.data;

public interface GenericData {

    default boolean isNull() {
        return false;
    }

    default boolean isSequence() {
        return false;
    }

    default boolean isKeyValue() {
        return false;
    }

    default boolean isPrimitive() {
        return false;
    }

    default boolean isReference() {
        return false;
    }

    default KeyValueData asKeyValue() {
        return (KeyValueData) this;
    }

    default SequenceData asSequence() {
        return (SequenceData) this;
    }

    default PrimitiveData asPrimitive() {
        return (PrimitiveData) this;
    }

    default ReferenceData asReference() {
        return (ReferenceData) this;
    }

    Object getInnerValue();
}
