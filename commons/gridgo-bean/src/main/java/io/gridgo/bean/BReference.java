package io.gridgo.bean;

import static io.gridgo.bean.support.BElementPojoHelper.anyToJsonElement;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.Consumer;

import io.gridgo.bean.exceptions.BeanSerializationException;
import io.gridgo.bean.factory.BFactory;
import io.gridgo.utils.wrapper.ByteBufferInputStream;
import lombok.NonNull;

public interface BReference extends BElement {

    static BReference of(Object reference) {
        return BFactory.DEFAULT.newReference(reference);
    }

    static BReference ofEmpty() {
        return BFactory.DEFAULT.newReference();
    }

    /**
     * return an instanceof BReference which deserialized from input stream. if
     * stream deserialized to key-value, it use BObject.toPojo() method. if stream
     * deserialized to breference which contains an instanceof targetType, return
     * itself.
     * 
     * @param inputStream
     * @param serializerName
     * @param targetType
     * @return
     * 
     * @throws if deserialized type is not reference or object
     */
    static BReference ofBytes(InputStream inputStream, String serializerName, Class<?> targetType) {
        var element = BFactory.DEFAULT.fromBytes(inputStream, serializerName);

        if (element.isReference() && element.asReference().referenceInstanceOf(targetType))
            return element.asReference();

        if (element.isObject())
            return BFactory.DEFAULT.newReference(element.asObject().toPojo(targetType));

        throw new BeanSerializationException(
                "Cannot convert input bytes as BReference of '" + targetType + "', deserialized: " + element);
    }

    static BReference ofBytes(byte[] bytes, String serializerName, Class<?> targetType) {
        return ofBytes(new ByteArrayInputStream(bytes), serializerName, targetType);
    }

    static BReference ofBytes(ByteBuffer buffer, String serializerName, Class<?> targetType) {
        return ofBytes(new ByteBufferInputStream(buffer), serializerName, targetType);
    }

    @Override
    default boolean isArray() {
        return false;
    }

    @Override
    default boolean isValue() {
        return false;
    }

    @Override
    default boolean isObject() {
        return false;
    }

    @Override
    default boolean isReference() {
        return true;
    }

    <T> T getReference();

    void setReference(Object reference);

    @Override
    default BType getType() {
        return BType.REFERENCE;
    }

    @SuppressWarnings("unchecked")
    default <T extends BElement> T deepClone() {
        return (T) of(this.getReference());
    }

    default Class<?> getReferenceClass() {
        if (this.getReference() == null) {
            return null;
        }
        return this.getReference().getClass();
    }

    default boolean referenceInstanceOf(Class<?> clazz) {
        var ref = this.getReference();
        if (clazz == null) {
            return ref == null;
        }
        if (ref != null) {
            return clazz.isAssignableFrom(ref.getClass());
        }
        return false;
    }

    default <T> void ifReferenceInstanceOf(Class<T> clazz, @NonNull Consumer<T> consumer) {
        if (referenceInstanceOf(clazz)) {
            consumer.accept(this.getReference());
        }
    }

    default <T> Optional<T> asOptional() {
        return Optional.ofNullable(this.getReference());
    }

    /**
     * Support for I/O operator when reference object is ByteBuffer or InputStream
     * or File
     * 
     * @param output
     * @return
     * @throws IOException
     */
    default boolean tryWriteNativeBytes(@NonNull OutputStream output) throws IOException {
        var ref = getReference();

        if (ref instanceof ByteBuffer) {
            try (var input = new ByteBufferInputStream((ByteBuffer) ref)) {
                input.transferTo(output);
                return true;
            }
        }

        if (ref instanceof InputStream) {
            ((InputStream) ref).transferTo(output);
            return true;
        }

        if (ref instanceof File) {
            try (var input = new FileInputStream((File) ref)) {
                input.transferTo(output);
                return true;
            }
        }

        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    default <T> T toJsonElement() {
        var ref = this.getReference();
        return ref == null ? null : (T) anyToJsonElement(this.getReference());
    }

    default BObject toBObject() {
        return BObject.ofPojo(this.getReference());
    }
}
