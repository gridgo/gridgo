package io.gridgo.utils.pojo.setter.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings({ "rawtypes", "unchecked" })
public interface SequenceData extends GenericData, Iterable<GenericData> {

    @Override
    default boolean isSequence() {
        return true;
    }

    GenericData get(int index);

    List toList();

    default Set toSet() {
        return new HashSet(this.toList());
    }

    @Override
    default Object getInnerValue() {
        return this.toList();
    }
}
