package io.gridgo.bean.test;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.BValue;
import io.gridgo.bean.test.support.Bar;

public class TestPrinter {

    public static void main(String[] args) {
        var raw = BValue.of(new byte[] { 1, 2, 3, 4, 5, 6 });
        var bar = new Bar();
        var ref = BReference.of(bar);
        var obj = BObject.ofEmpty() //
                         .setAny("ref", bar) //
                         .setAny("bool", false) //
                         .set("int", BValue.of(1)) //
                         .setAny("long", 1L) //
                         .setAny("char", 'a') //
                         .setAny("str", "hello") //
                         .setAny("double", 1.11) //
                         .setAny("byte", (byte) 1) //
                         .setAny("raw", raw) //
                         .setAny("arr", new int[] { 1, 2, 3 }) //
                         .set("obj", BObject.ofEmpty().setAny("int", 2)) //
        ;
        var arr = BArray.ofSequence(obj, 1, true, new byte[] { 4, 5, 6, 7 }, bar);

        System.out.println(raw);
        System.out.println(ref);
        System.out.println(obj);
        System.out.println(arr);
    }

}
