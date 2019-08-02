package io.gridgo.utils.helper;

public interface CastShortcut<Type> {

    @SuppressWarnings("unchecked")
    default <T extends Type> T cast() {
        return (T) this;
    }

    default <T extends Type> T castTo(Class<T> type) {
        return cast();
    }
}
