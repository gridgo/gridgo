package io.gridgo.core.support.transformers.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import io.gridgo.framework.support.Message;
import io.gridgo.utils.exception.RuntimeIOException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GzipDecompressMessageTransformer extends AbstractBytesMessageTransformer {

    @Override
    protected Message doTransform(Message msg, byte[] body) {
        try (var unzip = new GZIPInputStream(new ByteArrayInputStream(body))) {
            var unzippedBody = unzip.readAllBytes();
            return transformBody(msg, unzippedBody);
        } catch (IOException e) {
            throw new RuntimeIOException("Cannot transform GZIP decompression", e);
        }
    }
}
