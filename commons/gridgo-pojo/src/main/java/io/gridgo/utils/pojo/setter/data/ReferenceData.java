package io.gridgo.utils.pojo.setter.data;

public interface ReferenceData extends GenericData {

    @Override
    default boolean isNull() {
        return this.getReference() == null;
    }

    @Override
    default boolean isReference() {
        return true;
    }

    Object getReference();

    default Class<?> getReferenceClass() {
        var obj = getReference();
        return obj == null ? null : obj.getClass();
    }

    @Override
    default Object getInnerValue() {
        return this.getReference();
    }
}
