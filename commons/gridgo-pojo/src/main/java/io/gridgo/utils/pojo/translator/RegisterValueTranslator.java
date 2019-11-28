package io.gridgo.utils.pojo.translator;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.gridgo.utils.pojo.PojoMethodType;

@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface RegisterValueTranslator {

    String value();

    PojoMethodType defaultFor() default PojoMethodType.NONE;

    Class<?> defaultType() default Object.class;
}
