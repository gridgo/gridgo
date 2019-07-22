package io.gridgo.utils.pojo.asm;

import org.objectweb.asm.MethodVisitor;

import io.gridgo.utils.pojo.PojoMethodSignature;

public interface AsmMethodBuilder {

    void buildMethod(Class<?> type, PojoMethodSignature signature, MethodVisitor methodVisistor);
}
