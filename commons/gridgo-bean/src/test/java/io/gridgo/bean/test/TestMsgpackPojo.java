package io.gridgo.bean.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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

        BReference reference = BReference.of(foo);

        System.out.println(reference);

        byte[] bytes = reference.toBytes();

        System.out.println(new String(bytes));

        BObject obj = BElement.ofBytes(bytes);

        Foo foo2 = BElementPojoHelper.toPojo(obj, Foo.class);

        assertNotEquals(foo, foo2);

        foo.setArr(null);

        assertEquals(foo, foo2);
    }
}
