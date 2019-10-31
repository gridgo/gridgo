package io.gridgo.format.test;

import static io.gridgo.utils.PrimitiveUtils.getDoubleValueFrom;
import static io.gridgo.utils.format.CommonNumberTransformerRegistry.newXEvalExpTransformer;
import static io.gridgo.utils.format.StringFormatter.transform;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.gridgo.utils.format.GlobalFormatTransformerRegistry;

public class TestFormatTransformation {

    @Test
    public void testStringFormatter() {
        String str = "My name is {{ name > nameTransform }}, " //
                + "{{ age }} years old, " //
                + "monthly salary {{ salary > decrement10% > decrement50 > thousandSeparate}} {{currency > upperCase}}, " //
                + "health {{health > percentage}}";

        long now = 1572345589467l; // fixed value for unit test
        var obj = new Dummy("MY_NAME", 30, 10000000.97, "VND", "0.9756", now);

        var transformerRegistry = GlobalFormatTransformerRegistry.getInstance();
        transformerRegistry.addTransformer("decrement10%", newXEvalExpTransformer("0.9 * x"));
        transformerRegistry.addTransformer("decrement50", value -> getDoubleValueFrom(value) - 50);
        transformerRegistry.addAlias("nameTransform", "lowerCase > capitalize", "stripAccents");

        var expectedResult = "My name is My_name, 30 years old, monthly salary 8,999,950 VND, health 97.56%";
        var actual = transform(str, obj);
        assertEquals(expectedResult, actual);
    }
}
