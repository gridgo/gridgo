package io.gridgo.utils.helper;

import io.gridgo.utils.exception.AssertionException;
import lombok.NonNull;

public final class Assert {

    private Assert() {
    }

    public static void isTrue(boolean expectedTrue, String errorMessageFormat, Object... args) {
        errorIf(!expectedTrue, errorMessageFormat, args);
    }

    public static <T> T notNull(T reference, String parameterName) {
        errorIf(reference == null, parameterName + " cannot be null");
        return reference;
    }

    public static void state(boolean expression, String errorMessageFormat, Object... args) {
        if (!expression) {
            throw new IllegalStateException(String.format(errorMessageFormat, args));
        }
    }

    public static void errorIf(boolean expectedFalse, String errorMessageFormat, Object... args) {
        if (expectedFalse) {
            throw new AssertionException(errorMessageFormat == null ? "Assertion error" : String.format(errorMessageFormat, args));
        }
    }

    public static void notNegative(@NonNull Number number, String errorMessageFormat, Object... args) {
        errorIf(number.doubleValue() < 0, errorMessageFormat, args);
    }

    public static void notPositive(@NonNull Number number, String errorMessageFormat, Object... args) {
        errorIf(number.doubleValue() > 0, errorMessageFormat, args);
    }

    public static void notNegative(@NonNull Number number) {
        notNegative(number, null);
    }

    public static void notPositive(@NonNull Number number) {
        notPositive(number, null);
    }

}
