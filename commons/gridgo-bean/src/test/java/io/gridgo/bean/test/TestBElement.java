package io.gridgo.bean.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.stream.Collectors;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.factory.BFactory;

public class TestBElement {

    public static void main(String[] args) throws IOException {
        try (InputStream in = TestBElement.class.getClassLoader().getResourceAsStream("test.xml"); Reader reader = new InputStreamReader(in)) {

            String xml = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));

            BObject obj = BFactory.DEFAULT.fromXml(xml);
            System.out.println("Loaded object: " + obj);
            System.out.println("To XML: " + obj.toXml());

            byte[] bytes = obj.toBytes();
            System.out.println("Serialized: " + Arrays.toString(bytes));
            System.out.println("Serialized as string: " + new String(bytes));
            BElement fromRaw = BElement.ofBytes(bytes);
            System.out.println("Deserialized from raw: " + fromRaw);
        }
    }
}
