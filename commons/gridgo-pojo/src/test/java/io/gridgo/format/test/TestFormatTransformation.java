package io.gridgo.format.test;

import org.junit.Test;

import io.gridgo.format.CommonNumberTransformerRegistry;
import io.gridgo.format.GlobalFormatTransformerRegistry;
import io.gridgo.format.StringFormatter;
import io.gridgo.utils.PrimitiveUtils;

public class TestFormatTransformation {

    @Test
    public void testStringFormatter() {
        String str = "My name is {{ name > nameTransform }}, " //
                + "{{ age }} years old, " //
                + "monthly salary {{ salary > decrement10% > decrement50 > thousandSeparate}} {{currency > upperCase}}, " //
                + "health {{health > percentage}}, " //
                + "date: {{today > localOnlyDate}}, " //
                + "time: {{today > localOnlyTime12}}, " //
                + "fulltime: {{today > localFullTime24}}, " //
                + "fulltime gmt: {{today > gmtFullTime12}}";

        var obj = new Dummy("My Name", 30, 10000000.97, "VND", "0.9756");

        GlobalFormatTransformerRegistry.getInstance().addTransformer("decrement10%", CommonNumberTransformerRegistry.newXEvalExpTransformer("0.9 * x"));

        GlobalFormatTransformerRegistry.getInstance().addTransformer("decrement50", value -> PrimitiveUtils.getDoubleValueFrom(value) - 50);

        GlobalFormatTransformerRegistry.getInstance().addAlias("nameTransform", "lowerCase > capitalize", "stripAccents");

        System.out.println(StringFormatter.transform(str, obj));
    }
}
