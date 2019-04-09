package io.gridgo.framework.test;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BValue;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.MessageConstants;
import io.gridgo.framework.support.Payload;
import io.gridgo.framework.support.impl.MultipartMessage;

public class MessageUnitTest {

    @Test
    public void testMessage() {
        var msg1 = Message.of(Payload.of(BObject.ofEmpty().setAny("key", 1), BValue.of(1)));
        msg1.getPayload().addHeaderIfAbsent("key", 2);
        Assert.assertEquals(1, msg1.headers().getInteger("key").intValue());
        msg1.getPayload().addHeaderIfAbsent("test", 2);
        Assert.assertEquals(2, msg1.headers().getInteger("test").intValue());
        msg1.getPayload().addHeader("test", 1);
        Assert.assertEquals(1, msg1.headers().getInteger("test").intValue());
        msg1.attachSource("test");
        Assert.assertEquals("test", msg1.getMisc().get(MessageConstants.SOURCE));
        msg1.setRoutingIdFromAny(1);
        Assert.assertEquals(Integer.valueOf(1), msg1.getRoutingId().get().getInteger());
        var msg2 = Message.of(BValue.of(1), Payload.of(null));
        Assert.assertEquals(Integer.valueOf(1), msg2.getRoutingId().get().getInteger());
        var msg3 = Message.of(BValue.of(1), Collections.singletonMap("test", 1), Payload.of(null));
        Assert.assertEquals(1, msg3.getMisc().get("test"));
        var msg4 = Message.parse(BArray.ofEmpty().addAny(1).addAny(BObject.ofEmpty().setAny("test", 1)).addAny(1));
        Assert.assertEquals(Integer.valueOf(1), msg4.getPayload().getId().get().getInteger());
        Assert.assertEquals(Integer.valueOf(1), msg4.headers().getInteger("test"));
        Assert.assertEquals(Integer.valueOf(1), msg4.body().asValue().getInteger());
    }

    @Test
    public void testMultipart() {
        var msg1 = Message.of(Payload.of(BObject.ofEmpty().setAny("key", 1), BValue.of(1)));
        var msg2 = Message.of(Payload.of(BObject.ofEmpty().setAny("key", 2), BValue.of(2)));
        var multipart1 = new MultipartMessage(new Message[] { msg1, msg2 });
        assertMessage(multipart1);
        var multipart2 = new MultipartMessage((Iterable<Message>) Arrays.asList(new Message[] { msg1, msg2 }));
        assertMessage(multipart2);
    }

    private void assertMessage(MultipartMessage msg) {
        Assert.assertEquals(true, msg.headers().getBoolean(MessageConstants.IS_MULTIPART));
        Assert.assertEquals(2, msg.headers().getInteger(MessageConstants.SIZE).intValue());
        Assert.assertTrue(msg.body().isArray());
        Assert.assertEquals(2, msg.body().asArray().size());
        Assert.assertEquals(1,
                msg.body().asArray().getArray(0).getObject(1).getInteger("key").intValue());
        Assert.assertEquals(2,
                msg.body().asArray().getArray(1).getObject(1).getInteger("key").intValue());
        Assert.assertEquals(1, msg.body().asArray().getArray(0).getInteger(2).intValue());
        Assert.assertEquals(2, msg.body().asArray().getArray(1).getInteger(2).intValue());
    }

    @Test
    public void testPayload() {
        var payload = Payload.of(BValue.of(1), BValue.of(2));
        Assert.assertEquals(Integer.valueOf(1), payload.getId().get().getInteger());
        Assert.assertEquals(Integer.valueOf(2), payload.getBody().asValue().getInteger());
        payload.setIdFromAny("test");
        Assert.assertEquals("test", payload.getId().get().getString());
        payload.setBody(BObject.ofEmpty().setAny("test", 1));
        Assert.assertEquals(1, payload.getBody().asObject().getInteger("test").intValue());
    }
}
