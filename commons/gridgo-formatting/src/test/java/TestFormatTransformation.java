
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import io.gridgo.format.CommonNumberTransformerRegistry;
import io.gridgo.format.GlobalFormatTransformerRegistry;
import io.gridgo.format.StringFormatter;

public class TestFormatTransformation {

    public static void main(String[] args) {
        String str = "My name is {{ name > nameTransform }}, " //
                + "{{ age }} years old, " //
                + "monthly salary {{ salary > decrement10% > thousandSeparate}} {{currency > upperCase}}, " //
                + "health {{health > percentage}}, " //
                + "date: {{today > localOnlyDate}}, " //
                + "time: {{today > localOnlyTime12}}, " //
                + "fulltime: {{today > localFullTime24}}, " //
                + "fulltime gmt: {{today > gmtFullTime12}}";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "My Name");
        data.put("age", 30);
        data.put("salary", 10000000.97);
        data.put("currency", "VND");
        data.put("health", "0.9756");

        Supplier<Object> dateSupplier = Calendar.getInstance()::getTime;
        data.put("today", dateSupplier);

        GlobalFormatTransformerRegistry.getInstance().addTransformer("decrement10%", CommonNumberTransformerRegistry.newXEvalExpTransformer("0.9 * x"));

        GlobalFormatTransformerRegistry.getInstance().addAlias("nameTransform", "lowerCase > capitalize", "stripAccents");

        System.out.println(StringFormatter.transform(str, data));
    }
}
