package io.gridgo.bean.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.support.BElementPojoHelper;
import io.gridgo.bean.test.support.Bar;
import io.gridgo.bean.test.support.Foo;

public class TestMsgpackPojo {

    @Test
    public void testSerializePojo() {
        Foo foo = Foo.builder() //
                .arr(new int[] { 1, 2, 3, 4 }) //
                .d(0.123) //
                .b(Bar.builder() //
                        .b(true) //
                        .build()) //
                .build();

        byte[] bytes = BReference.of(foo).toBytes();
        BObject obj = BElement.ofBytes(bytes);

        Foo foo2 = BElementPojoHelper.toPojo(obj, Foo.class);

        assertEquals(foo, foo2);
    }
}
