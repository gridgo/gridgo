package io.gridgo.bean.test;

import org.junit.Test;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.test.support.Foo;

public class TestMsgpackPojo {

    @Test
    public void testSerializePojo() {
        byte[] bytes = BReference.of(new Foo()).toBytes();
        BObject obj = BElement.ofBytes(bytes);
        System.out.println(obj.toString());
    }
}
