package io.gridgo.utils.pojo.test.getter;

import org.junit.Test;

import io.gridgo.utils.pojo.getter.PojoGetterWalker;
import io.gridgo.utils.pojo.getter.PojoGetterWalkerBuilder;
import io.gridgo.utils.pojo.test.support.PrimitiveVO;

public class TestJavassistGetterWalker {

    @Test
    public void testSimple() {
        PojoGetterWalkerBuilder builder = PojoGetterWalkerBuilder.newJavassist();
        PojoGetterWalker walker = builder.buildGetterWalker(PrimitiveVO.class);
        walker.walkThroughFields(new PrimitiveVO(), (fieldName, value) -> {
            System.out.println(fieldName + " = " + value);
        }, "booleanValue", "intValue");
    }
}
