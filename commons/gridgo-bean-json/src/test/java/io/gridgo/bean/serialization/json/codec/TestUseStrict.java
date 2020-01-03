package io.gridgo.bean.serialization.json.codec;

import org.junit.Test;

import io.gridgo.bean.BElement;
import io.gridgo.bean.exceptions.BeanSerializationException;

public class TestUseStrict {

    @Test(expected = BeanSerializationException.class)
    public void testUseStrict() {
        BValueJsonCodec.USE_STRICT = true;
        BElement.ofJson("this is test text");
    }

    @Test
    public void testDontUseStrict() {
        BValueJsonCodec.USE_STRICT = false;
        BElement.ofJson("this is test text");
    }
}
