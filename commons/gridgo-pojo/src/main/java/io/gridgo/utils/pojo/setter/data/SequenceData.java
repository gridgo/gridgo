package io.gridgo.utils.pojo.setter.data;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@SuppressWarnings({ "rawtypes", "unchecked" })
public interface SequenceData extends GenericData, Iterable<GenericData> {

    @Override
    default boolean isSequence() {
        return true;
    }

    default List toList() {
        var list = new LinkedList<Object>();
        for (var data : this) {
            list.add(data.getInnerValue());
        }
        return list;
    }

    default Set toSet() {
        return new HashSet(this.toList());
    }

    @Override
    default Object getInnerValue() {
        return this.toList();
    }
}
