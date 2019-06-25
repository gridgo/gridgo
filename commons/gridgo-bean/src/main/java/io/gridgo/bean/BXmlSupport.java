package io.gridgo.bean;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import io.gridgo.bean.serialization.BSerializerRegistryAware;

public interface BXmlSupport extends BSerializerRegistryAware {

    default void writeXml(OutputStream out) {
        lookupSerializer("xml").serialize((BElement) this, out);
    }

    default String toXml() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.writeXml(out);
        return new String(out.toByteArray());
    }
}
