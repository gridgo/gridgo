package io.gridgo.bean.serialization.json.writer;

import java.io.IOException;

import io.gridgo.bean.BElement;

public interface ElementJsonWriter<T extends BElement> {

    public void writeElement(T element, Appendable outWriter) throws IOException;

    public Object toJsonElement(T element);
}