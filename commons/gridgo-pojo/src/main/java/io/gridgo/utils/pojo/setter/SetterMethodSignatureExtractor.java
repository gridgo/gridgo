package io.gridgo.utils.pojo.setter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Set;

import static io.gridgo.utils.StringUtils.lowerCaseFirstLetter;
import static io.gridgo.utils.pojo.PojoUtils.lookupValueTranslator;

import io.gridgo.utils.pojo.AbstractMethodSignatureExtractor;
import io.gridgo.utils.pojo.MethodSignatureExtractor;
import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.translator.UseValueTranslator;
import io.gridgo.utils.pojo.translator.ValueTranslator;

public class SetterMethodSignatureExtractor extends AbstractMethodSignatureExtractor {

    private static final String SETTER_PREFIX = "set";

    private static final MethodSignatureExtractor INSTANCE = new SetterMethodSignatureExtractor();

    public static final MethodSignatureExtractor getInstance() {
        return INSTANCE;
    }

    @Override
    protected boolean isApplicable(Method method, String methodName) {
        return method.getParameterCount() == 1 //
                && Modifier.isPublic(method.getModifiers()) //
                && method.getReturnType() == Void.TYPE //
                && methodName.startsWith(SETTER_PREFIX) && methodName.length() > 3;
    }

    @Override
    protected String extractFieldName(String methodName) {
        return lowerCaseFirstLetter(methodName.substring(3));
    }

    @Override
    protected PojoMethodSignature extract(Class<?> targetType, Method method, String fieldName, String transformRule,
            Set<String> ignoredFields) {
        Parameter param = method.getParameters()[0];
        Class<?> signatureType = param.getType();

        String transformedFieldName = transformFieldName(targetType, method, fieldName, transformRule, ignoredFields, signatureType);

        return PojoMethodSignature.builder() //
                .method(method) //
                .fieldType(signatureType) //
                .fieldName(fieldName) //
                .transformedFieldName(transformedFieldName) //
                .valueTranslator(extractValueTranslator(method, fieldName)) //
                .build();
    }

    private ValueTranslator extractValueTranslator(Method method, String fieldName) {
        if (method.isAnnotationPresent(UseValueTranslator.class))
            return lookupValueTranslator(method.getAnnotation(UseValueTranslator.class).value());

        try {
            var field = method.getDeclaringClass().getDeclaredField(fieldName);
            if (field.isAnnotationPresent(UseValueTranslator.class))
                return lookupValueTranslator(field.getAnnotation(UseValueTranslator.class).value());
        } catch (Exception e) {
            // do nothing
        }

        return null;
    }
}