package io.gridgo.pojo.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Retention(RUNTIME)
@Target({ TYPE, FIELD })
public @interface FieldAccess {

    public static enum FieldAccessLevel {
        ANY, PUBLIC, NONE;
    }

    @Getter
    @AllArgsConstructor
    public static enum FieldAccessMode {
        SET(true, false), GET(false, true), FULL(true, true);

        private final boolean setable;
        private final boolean getable;
    }

    FieldAccessLevel value();

    FieldAccessMode mode() default FieldAccessMode.FULL;
}
