package io.gridgo.utils.pojo.getter;

import static io.gridgo.utils.StringUtils.lowerCaseFirstLetter;
import static java.lang.reflect.Modifier.isPublic;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import io.gridgo.utils.pojo.AbstractMethodSignatureExtractor;
import io.gridgo.utils.pojo.IgnoreDefaultTranslator;
import io.gridgo.utils.pojo.IgnoreNull;
import io.gridgo.utils.pojo.MethodSignatureExtractor;
import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.translator.OnGetTranslate;
import io.gridgo.utils.pojo.translator.ValueTranslator;
import io.gridgo.utils.pojo.translator.ValueTranslators;
import lombok.NonNull;

public class GetterMethodSignatureExtractor extends AbstractMethodSignatureExtractor {

    private final static Set<String> GETTER_PREFIXES = new HashSet<String>(Arrays.asList("get", "is"));

    private static final MethodSignatureExtractor INSTANCE = new GetterMethodSignatureExtractor();

    public static final MethodSignatureExtractor getInstance() {
        return INSTANCE;
    }

    @Override
    protected String extractFieldName(String methodName) {
        int skip = methodName.startsWith("is") ? 2 : 3;
        return lowerCaseFirstLetter(methodName.substring(skip));
    }

    @Override
    protected PojoMethodSignature extract(Method method, String fieldName, String transformRule,
            Set<String> ignoredFields) {

        var returnType = method.getReturnType();
        var transformedFieldName = transformFieldName(method, fieldName, transformRule, ignoredFields, returnType);
        var ignoreNull = checkIgnoreNull(method, fieldName);

        return PojoMethodSignature.builder() //
                .method(method) //
                .fieldType(returnType) //
                .fieldName(fieldName) //
                .transformedFieldName(transformedFieldName) //
                .valueTranslator(extractValueTranslator(method, fieldName)) //
                .ignoreNull(ignoreNull) //
                .build();
    }

    private boolean checkIgnoreNull(@NonNull Method method, String fieldName) {
        if (method.getDeclaringClass().isAnnotationPresent(IgnoreNull.class))
            return true;

        if (method.isAnnotationPresent(IgnoreNull.class))
            return true;

        var field = getCorespondingField(method, fieldName);
        if (field != null && field.isAnnotationPresent(IgnoreNull.class))
            return true;

        return false;
    }

    @Override
    protected boolean isApplicable(@NonNull Method method) {
        var methodName = method.getName();
        var result = method.getParameterCount() == 0 //
                && isPublic(method.getModifiers()) //
                && method.getReturnType() != Void.TYPE //
                && GETTER_PREFIXES.stream().anyMatch(prefix -> methodName.startsWith(prefix));
        if (!result)
            return false;

        int skip = methodName.startsWith("is") ? 2 : 3;
        return methodName.length() > skip;
    }

    @SuppressWarnings("rawtypes")
    private ValueTranslator extractValueTranslator(Method method, String fieldName) {
        var annotationType = OnGetTranslate.class;
        if (method.isAnnotationPresent(annotationType)) {
            var key = method.getAnnotation(annotationType).value();
            return ValueTranslators.getInstance().lookupMandatory(key);
        }

        var field = getCorespondingField(method, fieldName);
        if (field != null && field.isAnnotationPresent(annotationType)) {
            var key = field.getAnnotation(annotationType).value();
            return ValueTranslators.getInstance().lookupMandatory(key);
        }

        var isIgnoreDefault = method.isAnnotationPresent(IgnoreDefaultTranslator.class) //
                || method.getDeclaringClass().isAnnotationPresent(IgnoreDefaultTranslator.class) //
                || (field != null && field.isAnnotationPresent(IgnoreDefaultTranslator.class));

        if (!isIgnoreDefault)
            return ValueTranslators.getInstance().lookupGetterDefault(method.getReturnType());

        return null;
    }
}
