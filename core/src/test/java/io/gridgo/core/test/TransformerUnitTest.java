package io.gridgo.core.test;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.core.support.transformers.impl.GzipDecompressMessageTransformer;
import io.gridgo.core.support.transformers.impl.GzipCompressMessageTransformer;
import io.gridgo.core.support.transformers.impl.WrappedMessageTransformer;
import io.gridgo.framework.support.Message;

public class TransformerUnitTest {

    @Test
    public void testWrapped() {
        var msg = Message.ofAny(1);
        var transformer = new WrappedMessageTransformer(
                this::incrementTransform,
                this::multiplyTransform
        );
        var result = transformer.transform(msg);
        Assert.assertEquals(Integer.valueOf(4), result.body().getInnerValue());
    }

    private Message incrementTransform(Message message) {
        return Message.ofAny(message.body().asValue().getInteger() + 1);
    }

    private Message multiplyTransform(Message message) {
        return Message.ofAny(message.body().asValue().getInteger() * 2);
    }

    @Test
    public void testZip() {
        var msg = Message.ofAny("test");
        var transformer = new WrappedMessageTransformer(
                new GzipCompressMessageTransformer(32),
                new GzipDecompressMessageTransformer()
        );
        var result = transformer.transform(msg);
        Assert.assertArrayEquals("test".getBytes(), result.body().getInnerValue());
    }
}
