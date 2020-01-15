package io.gridgo.core.support.transformers.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import io.gridgo.framework.support.Message;
import io.gridgo.utils.exception.RuntimeIOException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GzipCompressMessageTransformer extends AbstractBytesMessageTransformer {

    private int zipOutputDefaultSize;

    @Override
    protected Message doTransform(Message msg, byte[] body) {
        try (var baos = new ByteArrayOutputStream(zipOutputDefaultSize)) {
            try (var gzipStream = new GZIPOutputStream(baos)) {
                gzipStream.write(body);
            }
            return transformBody(msg, baos.toByteArray());
        } catch (IOException ex) {
            throw new RuntimeIOException("Cannot transform GZIP compression", ex);
        }
    }
}
