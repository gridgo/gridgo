package io.gridgo.bean;

import java.util.Optional;

public interface BArrayOptional {

    BArray getBArray();

    default Optional<BObject> getObject(int index) {
        return Optional.ofNullable(this.getBArray().getObject(index));
    }

    default Optional<BArray> getArray(int index) {
        return Optional.ofNullable(this.getBArray().getArray(index));
    }

    default Optional<BReference> getReference(int index) {
        return Optional.ofNullable(this.getBArray().getReference(index));
    }
}
