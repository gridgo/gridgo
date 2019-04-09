package io.gridgo.bean.test;

import java.io.ByteArrayOutputStream;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BObject;

public class TestWriteJsonOutputStream {

    public static void main(String[] args) {
        var data = BArray.ofSequence(null, BObject.ofEmpty(), "This is test text");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        data.writeJson(out);
        System.out.println(new String(out.toByteArray()));
    }
}
