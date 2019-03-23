package io.gridgo.boot.support.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.gridgo.framework.support.Registry;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Registries {

    Class<? extends Registry>[] registries() default {};
    
    String defaultProfile() default "";
}
