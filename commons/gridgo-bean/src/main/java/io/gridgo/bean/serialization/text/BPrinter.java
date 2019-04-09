package io.gridgo.bean.serialization.text;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Map.Entry;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.BType;
import io.gridgo.bean.BValue;
import io.gridgo.bean.exceptions.InvalidTypeException;
import io.gridgo.utils.StringUtils;
import io.gridgo.utils.exception.RuntimeIOException;
import lombok.NonNull;

public final class BPrinter {

    public static final void print(@NonNull OutputStream output, @NonNull BElement element) {
        try (var writer = new OutputStreamWriter(output)) {
            print(writer, element);
        } catch (IOException e) {
            throw new RuntimeIOException("Cannot write out belement", e);
        }
    }

    public static final void print(@NonNull Appendable writer, @NonNull BElement element) {
        try {
            printAny(writer, element, null, 0);
        } catch (IOException e) {
            throw new RuntimeIOException("Cannot write out belement", e);
        }
    }

    private static final void printAny(Appendable writer, BElement element, String name, int numTabs) throws IOException {
        if (element.isArray()) {
            printArray(writer, element.asArray(), name, numTabs);
        } else if (element.isObject()) {
            printObject(writer, element.asObject(), name, numTabs);
        } else if (element.isValue()) {
            printValue(writer, element.asValue(), name, numTabs);
        } else if (element.isReference()) {
            printReference(writer, element.asReference(), name, numTabs);
        } else {
            throw new InvalidTypeException("Cannot print BElement in type " + element.getClass().getName());
        }
    }

    private static final void printObject(Appendable writer, BObject object, String name, int numTab) throws IOException {
        StringUtils.tabs(numTab, writer);
        if (name != null) {
            writer.append(name).append(": OBJECT = {");
        } else {
            writer.append("{");
        }
        writer.append(object.size() > 0 ? "\n" : "");
        int count = 0;
        for (Entry<String, BElement> entry : object.entrySet()) {
            printAny(writer, entry.getValue(), entry.getKey(), numTab + 1);
            if (++count < object.size()) {
                writer.append(",\n");
            } else {
                writer.append("\n");
            }
        }
        if (object.size() > 0) {
            StringUtils.tabs(numTab, writer);
        }
        writer.append("}");
    }

    private static final void printArray(Appendable writer, BArray array, String name, int numTabs) throws IOException {
        StringUtils.tabs(numTabs, writer);
        if (name == null) {
            writer.append("[\n");
        } else {
            writer.append(name).append(": ARRAY = [\n");
        }
        for (int i = 0; i < array.size(); i++) {
            printAny(writer, array.get(i), "[" + i + "]", numTabs + 1);
            if (i < array.size() - 1) {
                writer.append(",\n");
            } else {
                writer.append("\n");
            }
        }
        StringUtils.tabs(numTabs, writer);
        writer.append("]");
    }

    private static final void printValue(Appendable writer, BValue value, String name, int numTab) throws IOException {
        StringUtils.tabs(numTab, writer);
        BType type = value.getType();
        String content = type == BType.RAW ? Arrays.toString(value.getRaw()) : value.getString();
        if (name == null) {
            writer.append("(").append(type.name()).append(" = ").append(content).append(")");
        } else {
            writer.append(name).append(": ").append(type.name());
            if (!value.isNull()) {
                writer.append(" = ").append(content);
            }
        }
    }

    private static final void printReference(Appendable writer, BReference reference, String name, int numTab) throws IOException {
        StringUtils.tabs(numTab, writer);
        BType type = reference.getType();
        Object refObj = reference.getReference();
        String content = (refObj == null ? "null" : refObj.getClass().getName());
        if (name == null) {
            writer.append("(").append(type.name()).append(" = ").append(content).append(")");
        } else {
            writer.append(name).append(": ").append(type.name()).append(" = ").append(content);
        }
    }
}
