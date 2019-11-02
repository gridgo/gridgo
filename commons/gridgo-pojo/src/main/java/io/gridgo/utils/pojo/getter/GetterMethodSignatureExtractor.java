package io.gridgo.utils.pojo.getter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static io.gridgo.utils.StringUtils.lowerCaseFirstLetter;

import io.gridgo.utils.pojo.AbstractMethodSignatureExtractor;
import io.gridgo.utils.pojo.MethodSignatureExtractor;
import io.gridgo.utils.pojo.PojoMethodSignature;

public class GetterMethodSignatureExtractor extends AbstractMethodSignatureExtractor {

    private final static Set<String> GETTER_PREFIXES = new HashSet<String>(Arrays.asList("get", "is"));

    private static final MethodSignatureExtractor INSTANCE = new GetterMethodSignatureExtractor();

    public static final MethodSignatureExtractor getInstance() {
        return INSTANCE;
    }

    @Override
    protected String extractFieldName(String methodName) {
        int skip = methodName.startsWith("is") ? 2 : 3;
        String fieldName = lowerCaseFirstLetter(methodName.substring(skip));
        return fieldName;
    }

    @Override
    protected PojoMethodSignature extract(Class<?> targetType, Method method, String fieldName, String transformRule,
            Set<String> ignoredFields) {
        Class<?> signatureType = method.getReturnType();

        String transformedFieldName = transformFieldName(targetType, method, fieldName, transformRule, ignoredFields, signatureType);

        var signature = PojoMethodSignature.builder() //
                .method(method) //
                .fieldType(signatureType) //
                .fieldName(fieldName) //
                .transformedFieldName(transformedFieldName) //
                .build();
        return signature;
    }

    @Override
    protected boolean isApplicable(Method method, String methodName) {
        var result = method.getParameterCount() == 0 //
                && Modifier.isPublic(method.getModifiers()) //
                && method.getReturnType() != Void.TYPE //
                && GETTER_PREFIXES.stream().anyMatch(prefix -> methodName.startsWith(prefix));
        if (!result)
            return false;

        int skip = methodName.startsWith("is") ? 2 : 3;
        return methodName.length() > skip;
    }
}
