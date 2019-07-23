package io.gridgo.utils.pojo.test.getter;

import org.junit.Test;

import io.gridgo.utils.pojo.getter.PojoGetterProxy;
import io.gridgo.utils.pojo.getter.PojoGetterProxyBuilder;
import io.gridgo.utils.pojo.test.support.PrimitiveVO;

public class TestJavassistGetterWalker {

    @Test
    public void testSimple() {
        PojoGetterProxyBuilder builder = PojoGetterProxyBuilder.newJavassist();
        PojoGetterProxy walker = builder.buildGetterWalker(PrimitiveVO.class);
        walker.walkThroughFields(new PrimitiveVO(), (fieldName, value) -> {
            System.out.println(fieldName + " = " + value);
        }, "booleanValue", "intValue");
    }
}
