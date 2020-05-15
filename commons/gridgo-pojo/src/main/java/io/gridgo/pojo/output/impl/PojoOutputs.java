package io.gridgo.pojo.output.impl;

import java.util.function.Function;

public class PojoOutputs {

    public static <T> PojoMapOutput<T> newMapOutput(Function<byte[], T> keyTranslator) {
        return new PojoMapOutput<T>(keyTranslator);
    }

    public static PojoMapOutput<byte[]> newMapOutputRaw() {
        return newMapOutput(Function.identity());
    }

    public static PojoMapOutput<String> newMapOutputString() {
        return newMapOutput(String::new);
    }

    public static <T> PojoListOutput<T> newListOutput(Function<byte[], T> keyTranslator) {
        return new PojoListOutput<T>(keyTranslator);
    }

    public static PojoListOutput<String> newListOutputString() {
        return newListOutput(String::new);
    }

    public static PojoListOutput<byte[]> newListOutputRaw() {
        return newListOutput(Function.identity());
    }
}
