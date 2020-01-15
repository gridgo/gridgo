package io.gridgo.utils.pojo.setter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.gridgo.utils.pojo.setter.PojoSetterProxyBuilder;
import io.gridgo.utils.pojo.support.PrimitiveVO;

public class TestJavassistSetterProxy {

    @Test
    public void testSimple() {
        PojoSetterProxyBuilder builder = PojoSetterProxyBuilder.newJavassist();
        var walker = builder.buildSetterProxy(PrimitiveVO.class);

        PrimitiveVO target = new PrimitiveVO();
        walker.applyValue(target, "intValue", 10);

        assertEquals(10, target.getIntValue());
    }
}
