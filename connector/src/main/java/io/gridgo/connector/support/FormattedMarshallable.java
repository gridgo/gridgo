package io.gridgo.connector.support;

import io.gridgo.bean.BElement;

public interface FormattedMarshallable {

    public default BElement deserialize(byte[] responseBody) {
        if (responseBody == null || responseBody.length == 0)
            return null;
        var format = getFormat();
        if (format == null)
            format = getDefaultFormat();
        return BElement.ofBytes(responseBody, format);
    }

    public default byte[] serialize(BElement body) {
        if (body == null || body.isNullValue())
            return null;
        var format = getFormat();
        if (format == null)
            format = getDefaultFormat();
        return body.toBytes(format);
    }

    public String getFormat();

    public default String getDefaultFormat() {
        return "json";
    }
}
