package io.gridgo.bean;

import java.util.Optional;

import lombok.NonNull;

public interface BObjectOptional {

    BObject getBObject();

    default Optional<BElement> get(@NonNull String field) {
        return Optional.ofNullable(this.getBObject().get(field));
    }

    default Optional<BObject> getObject(@NonNull String field) {
        return Optional.ofNullable(this.getBObject().getObject(field));
    }

    default Optional<BArray> getArray(@NonNull String field) {
        return Optional.ofNullable(this.getBObject().getArray(field));
    }

    default Optional<BReference> getReference(@NonNull String field) {
        return Optional.ofNullable(this.getBObject().getReference(field));
    }

    default Optional<BValue> getValue(@NonNull String field) {
        return Optional.ofNullable(this.getBObject().getValue(field));
    }

    default Optional<Boolean> getBoolean(@NonNull String field) {
        return Optional.ofNullable(this.getBObject().getBoolean(field));
    }

    default Optional<Character> getChar(@NonNull String field) {
        return Optional.ofNullable(this.getBObject().getChar(field));
    }

    default Optional<String> getString(@NonNull String field) {
        return Optional.ofNullable(this.getBObject().getString(field));
    }

    default Optional<byte[]> getRaw(@NonNull String field) {
        return Optional.ofNullable(this.getBObject().getRaw(field));
    }

    default Optional<Byte> getByte(@NonNull String field) {
        return Optional.ofNullable(this.getBObject().getByte(field));
    }

    default Optional<Short> getShort(@NonNull String field) {
        return Optional.ofNullable(this.getBObject().getShort(field));
    }

    default Optional<Integer> getInteger(@NonNull String field) {
        return Optional.ofNullable(this.getBObject().getInteger(field));
    }

    default Optional<Long> getLong(@NonNull String field) {
        return Optional.ofNullable(this.getBObject().getLong(field));
    }

    default Optional<Float> getFloat(@NonNull String field) {
        return Optional.ofNullable(this.getBObject().getFloat(field));
    }

    default Optional<Double> getDouble(@NonNull String field) {
        return Optional.ofNullable(this.getBObject().getDouble(field));
    }
}
