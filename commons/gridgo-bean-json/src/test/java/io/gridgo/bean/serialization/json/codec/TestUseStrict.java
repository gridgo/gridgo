package io.gridgo.bean.serialization.json.codec;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.Test;

import io.gridgo.bean.BElement;
import io.gridgo.bean.exceptions.BeanSerializationException;

public class TestUseStrict {

    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        // remove final modifier from field
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }

    private static void setUseStrict(boolean value) throws Exception {
        var field = BValueJsonCodec.class.getDeclaredField("USE_STRICT");
        setFinalStatic(field, value);
    }

    @Test(expected = BeanSerializationException.class)
    public void testUseStrict() throws Exception {
        setUseStrict(true);
        BElement.ofJson("this is test text");
    }

    @Test
    public void testDontUseStrict() throws Exception {
        setUseStrict(false);
        BElement.ofJson("this is test text");
        setUseStrict(true);
    }
}
