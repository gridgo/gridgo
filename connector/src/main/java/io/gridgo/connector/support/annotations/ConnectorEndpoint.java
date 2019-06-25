package io.gridgo.connector.support.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ConnectorEndpoint {

    public boolean raw() default false;

    public String scheme();

    public String syntax();

    public String category() default "";
}
