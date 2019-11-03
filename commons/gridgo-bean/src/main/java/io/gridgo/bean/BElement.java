package io.gridgo.bean;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.function.Consumer;
import java.util.function.Function;

import io.gridgo.bean.factory.BFactory;
import io.gridgo.bean.serialization.BSerializerRegistryAware;
import lombok.NonNull;

public interface BElement extends BSerializerRegistryAware, BJsonSupport, BXmlSupport, BBytesSupport {

    static <T extends BElement> T wrapAny(Object data) {
        return BFactory.DEFAULT.wrap(data);
    }

    static <T extends BElement> T ofAny(Object data) {
        return BFactory.DEFAULT.fromAny(data);
    }

    ////////////////// JSON Support//////////////////
    static <T extends BElement> T ofJson(String json) {
        return BFactory.DEFAULT.fromJson(json);
    }

    static <T extends BElement> T ofJson(InputStream inputStream) {
        return BFactory.DEFAULT.fromJson(inputStream);
    }

    ///////////////// Bytes Support////////////////////
    static <T extends BElement> T ofBytes(@NonNull InputStream in, String serializerName) {
        return BFactory.DEFAULT.fromBytes(in, serializerName);
    }

    static <T extends BElement> T ofBytes(@NonNull ByteBuffer buffer, String serializerName) {
        return BFactory.DEFAULT.fromBytes(buffer, serializerName);
    }

    static <T extends BElement> T ofBytes(@NonNull byte[] bytes, String serializerName) {
        return BFactory.DEFAULT.fromBytes(bytes, serializerName);
    }

    static <T extends BElement> T ofBytes(@NonNull InputStream in) {
        return ofBytes(in, null);
    }

    static <T extends BElement> T ofBytes(@NonNull ByteBuffer buffer) {
        return ofBytes(buffer, null);
    }

    static <T extends BElement> T ofBytes(@NonNull byte[] bytes) {
        return ofBytes(bytes, null);
    }

    boolean isContainer();

    default boolean isNullValue() {
        return this.isValue() && this.asValue().isNull();
    }

    boolean isArray();

    default <T> T isArrayThen(Function<BArray, T> handler) {
        if (this.isArray()) {
            return handler.apply(this.asArray());
        }
        return null;
    }

    default void isArrayThen(Consumer<BArray> handler) {
        if (this.isArray()) {
            handler.accept(this.asArray());
        }
    }

    boolean isObject();

    default <T> T isObjectThen(Function<BObject, T> handler) {
        if (this.isObject()) {
            return handler.apply(this.asObject());
        }
        return null;
    }

    default void isObjectThen(Consumer<BObject> handler) {
        if (this.isObject()) {
            handler.accept(this.asObject());
        }
    }

    boolean isValue();

    default <T> T isValueThen(Function<BValue, T> handler) {
        if (this.isValue()) {
            return handler.apply(this.asValue());
        }
        return null;
    }

    default void isValueThen(Consumer<BValue> handler) {
        if (this.isValue()) {
            handler.accept(this.asValue());
        }
    }

    boolean isReference();

    default <T> T isReferenceThen(Function<BReference, T> handler) {
        if (this.isReference()) {
            return handler.apply(this.asReference());
        }
        return null;
    }

    default void isReferenceThen(Consumer<BReference> handler) {
        if (this.isReference()) {
            handler.accept(this.asReference());
        }
    }

    default BContainer asContainer() {
        return (BContainer) this;
    }

    default BObject asObject() {
        return (BObject) this;
    }

    default BArray asArray() {
        return (BArray) this;
    }

    default BValue asValue() {
        return (BValue) this;
    }

    default BReference asReference() {
        return (BReference) this;
    }

    BType getType();

    <T extends BElement> T deepClone();

    <T> T getInnerValue();
}
