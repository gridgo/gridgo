package io.gridgo.pojo.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.gridgo.pojo.field.PojoInstrumentor;

@Retention(RUNTIME)
@Target({ FIELD, METHOD })
@Repeatable(FieldTags.class)
public @interface FieldTag {

    String tag() default "";

    String name() default "";

    @SuppressWarnings("rawtypes")
    Class<? extends PojoInstrumentor> instrumentor() default PojoInstrumentor.class;
}
