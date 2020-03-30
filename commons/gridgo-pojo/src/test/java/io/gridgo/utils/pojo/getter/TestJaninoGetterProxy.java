package io.gridgo.utils.pojo.getter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.gridgo.utils.pojo.support.PrimitiveVO;

public class TestJaninoGetterProxy {

    @Test
    public void testSimple() {
        var builder = PojoGetterProxyBuilder.newJanino();
        var proxy = builder.buildGetterProxy(PrimitiveVO.class);

        PrimitiveVO target = new PrimitiveVO();
        target.setIntValue(111);

        assertEquals(111, proxy.getValue(target, "intValue"));
    }
}
