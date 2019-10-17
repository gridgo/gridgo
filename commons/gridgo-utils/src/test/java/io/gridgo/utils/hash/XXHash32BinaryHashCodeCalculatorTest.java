package io.gridgo.utils.hash;

import net.jpountz.xxhash.XXHashFactory;
import org.junit.Test;

import static org.junit.Assert.*;

public class XXHash32BinaryHashCodeCalculatorTest {

    @Test(expected = NullPointerException.class)
    public void constructor_ShouldThrowException_WhenHasherIsNull() {
        new XXHash32BinaryHashCodeCalculator(null, 1);
    }

    @Test
    public void calcHashCode() {
        int id = 10;
        var hashCodeCalculator = new XXHash32BinaryHashCodeCalculator(XXHashFactory.safeInstance().hash32(), id);
        var bytes = new byte[] {0x00, 0x01, 0x02, 0x03};
        var ans = hashCodeCalculator.calcHashCode(bytes);
        assertEquals(1256378755, ans);
        assertEquals(id, hashCodeCalculator.getId());
    }
}