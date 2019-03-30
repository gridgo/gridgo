package io.gridgo.boot.support;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LazyInitializer {

    private Class<?> gatewayClass;

    private Object instance;
}
