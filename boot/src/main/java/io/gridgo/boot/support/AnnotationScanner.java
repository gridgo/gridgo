package io.gridgo.boot.support;

import java.util.List;

import org.reflections.Reflections;

import io.gridgo.core.GridgoContext;

public interface AnnotationScanner {

    public void scanAnnotation(Reflections ref, GridgoContext context, List<LazyInitializer> lazyInitializers);
}
