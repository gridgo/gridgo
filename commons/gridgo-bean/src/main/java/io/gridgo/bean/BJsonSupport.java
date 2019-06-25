package io.gridgo.bean;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import io.gridgo.bean.exceptions.InvalidTypeException;
import io.gridgo.bean.serialization.BSerializerRegistryAware;

public interface BJsonSupport extends BSerializerRegistryAware {

    <T> T toJsonElement();

    default void writeJson(OutputStream out) {
        if (this instanceof BElement)
            lookupSerializer("json").serialize((BElement) this, out);
        else
            throw new InvalidTypeException("writeJson by default only support BElement");
    }

    default String toJson() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeJson(out);
        return new String(out.toByteArray());
    }
}
