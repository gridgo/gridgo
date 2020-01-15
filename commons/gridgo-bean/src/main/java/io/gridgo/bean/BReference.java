package io.gridgo.bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.Consumer;

import io.gridgo.bean.factory.BFactory;
import io.gridgo.utils.annotations.Transient;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;
import io.gridgo.utils.pojo.getter.PojoGetterRegistry;
import io.gridgo.utils.pojo.setter.PojoSetterProxy;
import io.gridgo.utils.pojo.setter.PojoSetterRegistry;
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
     * @param inputStream    input stream
     * @param serializerName serialzier name, null mean default (msgpack)
     * @param targetType     target type
     * @return BReference which contains instance of targetType
     *
     */
    static BReference ofBytes(InputStream inputStream, String serializerName, Class<?> targetType) {
        return BFactory.DEFAULT.fromBytes(inputStream, serializerName, targetType);
    }

    static BReference ofBytes(byte[] bytes, String serializerName, Class<?> targetType) {
        return ofBytes(new ByteArrayInputStream(bytes), serializerName, targetType);
    }

    static BReference ofBytes(ByteBuffer buffer, String serializerName, Class<?> targetType) {
        return ofBytes(new ByteBufferInputStream(buffer), serializerName, targetType);
    }

    @Override
    @Transient
    default boolean isReference() {
        return true;
    }

    <T> T getReference();

    void setReference(Object reference);

    @Override
    default BType getType() {
        return BType.REFERENCE;
    }

    @Override
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

    default boolean referenceInstanceOf(@NonNull Class<?> clazz) {
        var ref = this.getReference();
        if (ref != null)
            return clazz.isInstance(ref);
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
     * @param output where data will be write to
     * @return success or not
     * @throws IOException if output stream cannot be written
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

    default BObject toBObject() {
        return BObject.ofPojo(this.getReference());
    }

    @Override
    default <T> T getInnerValue() {
        return getReference();
    }

    default PojoGetterProxy getterProxy() {
        if (this.getReference() == null)
            return null;
        return PojoGetterRegistry.DEFAULT.getGetterProxy(getReferenceClass());
    }

    default PojoSetterProxy setterProxy() {
        if (this.getReference() == null)
            return null;
        return PojoSetterRegistry.DEFAULT.getSetterProxy(getReferenceClass());
    }

    void getterProxy(PojoGetterProxy getterProxy);

    void setterProxy(PojoSetterProxy setterProxy);
}
