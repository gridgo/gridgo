package io.gridgo.boot.data.support.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.gridgo.boot.data.support.DataAccessHandler;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataAccess {

    public String gateway();

    public Class<? extends DataAccessHandler> handler();
}
