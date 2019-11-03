package io.gridgo.bean;

import java.util.Optional;

public interface BArrayOptional {

    BArray getBArray();

    default Optional<BValue> getValue(int index) {
        var arr = getBArray();
        if (index < 0 || index >= arr.size())
            return Optional.empty();
        return Optional.ofNullable(arr.getValue(index));
    }

    default Optional<BObject> getObject(int index) {
        var arr = getBArray();
        if (index < 0 || index >= arr.size())
            return Optional.empty();
        return Optional.ofNullable(arr.getObject(index));
    }

    default Optional<BArray> getArray(int index) {
        var arr = getBArray();
        if (index < 0 || index >= arr.size())
            return Optional.empty();
        return Optional.ofNullable(arr.getArray(index));
    }

    default Optional<BReference> getReference(int index) {
        var arr = getBArray();
        if (index < 0 || index >= arr.size())
            return Optional.empty();
        return Optional.ofNullable(arr.getReference(index));
    }
}
