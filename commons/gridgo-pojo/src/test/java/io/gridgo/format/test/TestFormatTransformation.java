package io.gridgo.format.test;

import static io.gridgo.utils.PrimitiveUtils.getDoubleValueFrom;
import static io.gridgo.utils.format.StringFormatter.transform;
import static org.junit.Assert.assertEquals;

import java.text.DecimalFormat;

import org.junit.Test;

import io.gridgo.utils.format.GlobalFormatTransformerRegistry;
import io.gridgo.utils.format.StringFormatter;
import io.gridgo.utils.format.StringFormatter.StringFormatOption;

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
        transformerRegistry.addTransformer("decrement10%", value -> Double.valueOf((double) value * 0.9).longValue());
        transformerRegistry.addTransformer("decrement50", value -> getDoubleValueFrom(value) - 50);
        transformerRegistry.addAlias("nameTransform", "lowerCase > capitalize", "stripAccents");

        var expectedResult = "My name is My_name, 30 years old, monthly salary 8,999,950 VND, health 97.56%";
        var actual = transform(str, obj);
        assertEquals(expectedResult, actual);
        actual = transform("{{name}}", obj, null);
        assertEquals("MY_NAME", actual);
        var option = StringFormatOption.builder().decimalFormat(new DecimalFormat("###,###.##")).autoFormatNumber(true)
                .build();
        actual = StringFormatter.format("{{name}} {{salary}}", obj, option);
        assertEquals("MY_NAME 10,000,000.97", actual);
        option = StringFormatOption.builder().autoFormatNumber(true).build();
        actual = StringFormatter.format("{{name}} {{salary}}", obj, option);
        assertEquals("MY_NAME 10,000,000.97", actual);
    }
}
