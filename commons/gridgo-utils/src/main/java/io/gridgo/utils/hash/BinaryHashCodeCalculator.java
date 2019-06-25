package io.gridgo.utils.hash;

import java.util.Map;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import net.jpountz.xxhash.XXHashFactory;

public abstract class BinaryHashCodeCalculator implements HashCodeCalculator<byte[]> {

    public static final BinaryHashCodeCalculator DEFAULT = new BinaryHashCodeCalculator() {

        @Override
        public int getId() {
            return 0;
        };

        @Override
        public int calcHashCode(byte[] bytes) {
            if (bytes == null)
                return 0;

            int result = 1;
            for (byte element : bytes)
                result = (result << 5) - result + element;

            return result;
        }
    };

    public static final BinaryHashCodeCalculator REVERSED = new BinaryHashCodeCalculator() {

        @Override
        public int getId() {
            return 1;
        };

        @Override
        public int calcHashCode(byte[] bytes) {
            if (bytes == null)
                return 0;

            int result = 1;
            for (int i = bytes.length - 1; i >= 0; i--)
                result = (result << 5) - result + bytes[i];

            return result;
        }
    };

    public static final BinaryHashCodeCalculator XXHASH32_JAVA_UNSAFE = new XXHash32BinaryHashCodeCalculator(
            XXHashFactory.unsafeInstance().hash32(), 2);

    public static final BinaryHashCodeCalculator XXHASH32_JAVA_SAFE = new XXHash32BinaryHashCodeCalculator(
            XXHashFactory.safeInstance().hash32(), 3);

    public static final BinaryHashCodeCalculator XXHASH32_JNI = new XXHash32BinaryHashCodeCalculator(
            XXHashFactory.fastestInstance().hash32(), 4);

    private static final Map<Integer, BinaryHashCodeCalculator> binaryHashCodeCalculatorRegistry = new NonBlockingHashMap<>();

    public static final BinaryHashCodeCalculator getRegisteredCalculator(int id) {
        return binaryHashCodeCalculatorRegistry.get(id);
    }

    public static final void registerCalculator(BinaryHashCodeCalculator calculator) {
        if (calculator == null) {
            throw new NullPointerException("Cannot register null");
        }
        synchronized (binaryHashCodeCalculatorRegistry) {
            if (binaryHashCodeCalculatorRegistry.containsKey(calculator.getId())) {
                throw new RuntimeException(
                        "BinaryHashCodeCalculator with id " + calculator.getId() + " has been already registerd");
            }
            binaryHashCodeCalculatorRegistry.put(calculator.getId(), calculator);
        }
    }

    static {
        registerCalculator(DEFAULT);
        registerCalculator(REVERSED);
        registerCalculator(XXHASH32_JAVA_UNSAFE);
        registerCalculator(XXHASH32_JAVA_SAFE);
        registerCalculator(XXHASH32_JNI);
    }

    public abstract int getId();
}
