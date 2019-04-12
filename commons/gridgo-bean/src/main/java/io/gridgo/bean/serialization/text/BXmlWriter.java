package io.gridgo.bean.serialization.text;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map.Entry;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.BValue;
import io.gridgo.bean.exceptions.InvalidTypeException;
import io.gridgo.utils.ByteArrayUtils;
import io.gridgo.utils.exception.RuntimeIOException;

class BXmlWriter {

    public static void write(OutputStream out, BElement element) {
        final var output = new OutputStreamWriter(out);
        try {
            write(output, element);
            output.flush();
        } catch (IOException e) {
            throw new RuntimeIOException("Error while write out xml", e);
        }
    }

    public static void write(Appendable output, BElement element) {
        try {
            writeAny(output, element, null);
        } catch (IOException e) {
            throw new RuntimeIOException("Error while write out xml", e);
        }
    }

    private static void writeAny(Appendable output, BElement element, String name) throws IOException {
        if (element.isArray()) {
            writeArray(output, element.asArray(), name);
        } else if (element.isObject()) {
            writeObject(output, element.asObject(), name);
        } else if (element.isValue()) {
            writeValue(output, element.asValue(), name);
        } else if (element.isReference()) {
            writeReference(output, element.asReference(), name);
        } else {
            throw new InvalidTypeException("Xml writer by default only support BObject, BArray and BValue");
        }
    }

    private static void writeObject(Appendable out, BObject object, String name) throws IOException {
        if (name == null)
            out.append("<object>");
        else
            out.append("<object name=\"").append(name).append("\">");

        for (Entry<String, BElement> entry : object.entrySet()) {
            writeAny(out, entry.getValue(), entry.getKey());
        }

        out.append("</object>");
    }

    private static void writeArray(Appendable out, BArray array, String name) throws IOException {
        if (name != null) {
            out.append("<array name=\"").append(name).append("\">");
        } else {
            out.append("<array>");
        }
        for (BElement element : array) {
            writeAny(out, element, null);
        }
        out.append("</array>");
    }

    private static void writeValue(Appendable out, BValue value, String name) throws IOException {
        if (!value.isNull()) {
            String type = value.getType().name().toLowerCase();
            out.append("<").append(type);
            if (name != null) {
                out.append(" name=\"").append(name).append("\"");
            }
            String content = value.getData() instanceof byte[] ? ByteArrayUtils.toHex(value.getRaw(), "0x") : value.getString();
            if (content.contains("<")) {
                out.append(">") //
                   .append("<![CDATA[")//
                   .append(content) //
                   .append("]]>") //
                   .append("</").append(type).append(">");
            } else if (content.contains("\"")) {
                out.append(">") //
                   .append(content) //
                   .append("</").append(type).append(">");
            } else {
                out.append(" value=\"").append(content.replaceAll("\"", "\\\"")).append("\"/>");
            }
        } else {
            if (name == null) {
                out.append("<null />");
            } else {
                out.append("<null name=\"").append(name).append("\" />");
            }
        }
    }

    private static void writeReference(Appendable out, BReference reference, String name) throws IOException {
        if (name != null) {
            out.append("<reference name=\"").append(name).append("\">");
        } else {
            out.append("<reference>");
        }
        out.append(reference.toString());
        out.append("</reference>");
    }
}
