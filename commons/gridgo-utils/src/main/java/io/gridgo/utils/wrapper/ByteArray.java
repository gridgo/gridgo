package io.gridgo.utils.wrapper;

import static io.gridgo.utils.hash.BinaryHashCodeCalculator.DEFAULT;
import static io.gridgo.utils.hash.BinaryHashCodeCalculator.XXHASH32_JAVA_SAFE;
import static io.gridgo.utils.hash.BinaryHashCodeCalculator.XXHASH32_JAVA_UNSAFE;
import static io.gridgo.utils.hash.BinaryHashCodeCalculator.XXHASH32_JNI;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Base64;

import io.gridgo.utils.hash.BinaryHashCodeCalculator;

public class ByteArray implements Serializable, Comparable<ByteArray> {

    private static final long serialVersionUID = -888719399858981241L;

    public static final ByteArray newInstance(byte[] source) {
        return new ByteArray(source);
    }

    public static final ByteArray newInstance(byte[] source, BinaryHashCodeCalculator hashCodeCalculator) {
        return new ByteArray(source, hashCodeCalculator);
    }

    public static final ByteArray newInstanceWithJavaSafeHashCodeCalculator(byte[] source) {
        return ByteArray.newInstance(source, XXHASH32_JAVA_SAFE);
    }

    public static final ByteArray newInstanceWithJavaUnsafeHashCodeCalculator(byte[] source) {
        return ByteArray.newInstance(source, XXHASH32_JAVA_UNSAFE);
    }

    public static final ByteArray newInstanceWithJNIHashCodeCalculator(byte[] source) {
        return ByteArray.newInstance(source, XXHASH32_JNI);
    }

    private final byte[] source;

    private final int hashCodeCalculatorId;

    private transient String cachedString = null;
    
    private transient String cachedBase64 = null;
    
    private transient Integer cachedHashCode = null;
    
    private transient BinaryHashCodeCalculator hashCodeCalculator = null;

    private ByteArray(byte[] source) {
        this(source, DEFAULT);
    }

    private ByteArray(byte[] source, BinaryHashCodeCalculator hashCodeCalculator) {
        if (source == null) {
            throw new NullPointerException("Source byte[] cannot be null");
        }
        if (hashCodeCalculator == null) {
            throw new NullPointerException("Hash code caculator cannot be null");
        }
        this.source = source;
        this.hashCodeCalculator = hashCodeCalculator;
        this.hashCodeCalculatorId = hashCodeCalculator.getId();
    }

    protected BinaryHashCodeCalculator getHashCodeCalculator() {
        if (this.hashCodeCalculator == null) {
            this.hashCodeCalculator = BinaryHashCodeCalculator.getRegisteredCalculator(hashCodeCalculatorId);
            if (this.hashCodeCalculator == null) {
                this.hashCodeCalculator = BinaryHashCodeCalculator.DEFAULT;
            }
        }
        return hashCodeCalculator;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ByteArray) {
            return Arrays.equals(source, ((ByteArray) other).source);
        } else if (other instanceof byte[]) {
            return Arrays.equals(source, (byte[]) other);
        }
        return false;
    }

    public byte[] getSource() {
        return this.source;
    }

    @Override
    public int hashCode() {
        if (cachedHashCode == null) {
            cachedHashCode = this.getHashCodeCalculator().calcHashCode(this.source);
        }
        return cachedHashCode;
    }

    @Override
    public String toString() {
        if (this.cachedString == null) {
            this.cachedString = Arrays.toString(this.source);
        }
        return this.cachedString;
    }

    public String toBase64() {
        if (this.cachedBase64 == null) {
            this.cachedBase64 = Base64.getEncoder().encodeToString(getSource());
        }
        return this.cachedBase64;
    }

    @Override
    public int compareTo(ByteArray o) {
        return Arrays.equals(this.getSource(), o.getSource()) ? 0 : (this.hashCode() > o.hashCode() ? 1 : -1);
    }

}
