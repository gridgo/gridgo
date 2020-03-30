package io.gridgo.utils.pojo;

import java.util.List;

public interface MethodSignatureExtractor {

    public List<PojoFieldSignature> extractMethodSignatures(Class<?> targetType);
}
