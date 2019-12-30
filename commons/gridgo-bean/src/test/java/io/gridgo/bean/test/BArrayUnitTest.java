package io.gridgo.bean.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BType;

public class BArrayUnitTest {

    @Test(expected = IndexOutOfBoundsException.class)
    public void testNullOrMissing() {
        var arr = BArray.ofEmpty();
        arr.getInteger(0);
    }

    @Test
    public void testWriteString() {
        var arr = BArray.ofSequence(1, 2, 3);
        Assert.assertNotNull(arr.toString());
    }

    @Test
    public void testWrapAndHolder() {
        var list = Arrays.asList(1, 2, 3, 4);
        var arr = BArray.wrap(list);
        Assert.assertEquals(list, arr.toList());

        arr = BArray.withHolder(new EmptyList<>());
        arr.addAny(1);
        Assert.assertTrue(arr.isEmpty());
    }

    @Test
    public void testFromBArray() {
        var obj1 = BArray.ofEmpty();
        var obj2 = BArray.of(obj1);
        Assert.assertTrue(obj1 == obj2);
    }

    @Test
    public void testTypeOf() {
        var arr = BArray.ofSequence("v1", 2, BArray.ofEmpty(), BObject.ofEmpty());
        Assert.assertEquals(BType.STRING, arr.typeOf(0));
        Assert.assertEquals(BType.INTEGER, arr.typeOf(1));
        Assert.assertEquals(BType.ARRAY, arr.typeOf(2));
        Assert.assertEquals(BType.OBJECT, arr.typeOf(3));
    }

    @Test
    public void testOptional() {
        var arr = BArray.ofSequence("v1", 2, BArray.ofEmpty(), BObject.ofEmpty(), new Object());
        var optional = arr.asOptional();
        Assert.assertTrue(optional.getValue(0).isPresent());
        Assert.assertTrue(optional.getValue(1).isPresent());
        Assert.assertTrue(optional.getArray(2).isPresent());
        Assert.assertTrue(optional.getObject(3).isPresent());
        Assert.assertTrue(optional.getReference(4).isPresent());
        Assert.assertTrue(optional.getValue(5).isEmpty());
        Assert.assertTrue(optional.getArray(5).isEmpty());
        Assert.assertTrue(optional.getObject(5).isEmpty());
        Assert.assertTrue(optional.getReference(5).isEmpty());
        Assert.assertTrue(optional.getValue(-1).isEmpty());
        Assert.assertTrue(optional.getArray(-1).isEmpty());
        Assert.assertTrue(optional.getObject(-1).isEmpty());
        Assert.assertTrue(optional.getReference(-1).isEmpty());
    }

    class EmptyList<K> extends ArrayList<K> {

        private static final long serialVersionUID = -1298127233576511932L;

        @Override
        public boolean add(K value) {
            return true;
        }
    }
}
