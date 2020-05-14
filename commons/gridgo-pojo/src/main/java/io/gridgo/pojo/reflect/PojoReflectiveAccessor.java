package io.gridgo.pojo.reflect;

import static io.gridgo.utils.StringUtils.upperCaseFirstLetter;

import java.lang.reflect.Field;

import org.apache.commons.lang3.ArrayUtils;

import io.gridgo.pojo.annotation.FieldRef;
import io.gridgo.pojo.annotation.FieldTag;
import io.gridgo.pojo.reflect.type.PojoSimpleType;
import io.gridgo.pojo.reflect.type.PojoType;
import io.gridgo.pojo.reflect.type.PojoTypeResolver;
import io.gridgo.pojo.support.PojoAccessorType;
import io.gridgo.utils.pojo.exception.PojoException;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

public interface PojoReflectiveAccessor {

    String fieldName();

    PojoType fieldType();

    Field refField();

    PojoReflectiveElement element();

    PojoAccessorType type();

    FieldTag[] tags();

    default boolean isSetter() {
        return type() == PojoAccessorType.SET;
    }

    default boolean isGetter() {
        return type() == PojoAccessorType.GET;
    }
}

@Getter
@Accessors(fluent = true)
abstract class AbstractPojoReflectiveAccessor implements PojoReflectiveAccessor {

    private final @NonNull PojoAccessorType type;

    private final @NonNull String fieldName;

    private final @NonNull PojoReflectiveElement element;

    private final Field refField;

    private final FieldTag[] tags;

    protected AbstractPojoReflectiveAccessor(PojoAccessorType type, String fieldName, PojoReflectiveElement element) {
        this.type = type;
        this.fieldName = fieldName;
        this.element = element;
        this.refField = findRefField();
        this.tags = extractTags();
    }

    private Field findRefField() {
        var refField = findRefFieldViaAnnotation();

        if (refField == null) {
            var fieldName = fieldName();
            var fieldType = fieldType();

            // find field named like "somthing"
            refField = getDeclaredFieldIfExist(fieldName);

            var fieldRawType = (fieldType instanceof PojoSimpleType) ? ((PojoSimpleType) fieldType).type() : null;
            if (refField == null && (fieldRawType == boolean.class || fieldRawType == Boolean.class)) {
                // try to prepend "is" to find field name like "isSomething"
                fieldName = "is" + upperCaseFirstLetter(fieldName);
                refField = getDeclaredFieldIfExist(fieldName);
            }

            if (refField != null) {
                var refTypeInfo = PojoTypeResolver.extractFieldTypeInfo(refField, element.effectiveClass());
                if (refField != null && !fieldType.equals(refTypeInfo))
                    refField = null;
            }
        }

        return refField;
    }

    private Field findRefFieldViaAnnotation() {
        if (!element.isMethod())
            return null;

        var refFieldAnnotation = element.method().getAnnotation(FieldRef.class);
        if (refFieldAnnotation == null)
            return null;

        var refFieldName = refFieldAnnotation.value();
        try {
            return element.declaringClass().getDeclaredField(refFieldName);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new PojoException("Field not found for annotated @FieldRef value '" + refFieldName + "' on method "
                    + element.name() + ", type " + element.declaringClass().getName());
        }
    }

    private Field getDeclaredFieldIfExist(String fieldName) {
        try {
            return element.declaringClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException | SecurityException e) {
            // do nothing
        }
        return null;
    }

    private FieldTag[] extractTags() {
        var element = element();

        if (element.isField())
            return element.field().getAnnotationsByType(FieldTag.class);

        var tags = element.method().getAnnotationsByType(FieldTag.class);
        if (refField != null)
            return ArrayUtils.addAll(tags, refField.getAnnotationsByType(FieldTag.class));

        return tags;
    }
}