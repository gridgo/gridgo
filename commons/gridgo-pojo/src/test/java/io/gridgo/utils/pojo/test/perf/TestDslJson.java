package io.gridgo.utils.pojo.test.perf;

import java.text.DecimalFormat;
import java.util.Map;

import org.junit.Test;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.JsonWriter.WriteObject;

import io.gridgo.utils.CollectionUtils;
import io.gridgo.utils.MapUtils;
import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.test.support.Bar;
import io.gridgo.utils.pojo.test.support.Foo;

public class TestDslJson {

    private final Foo obj;

    private final DslJson<Foo> dslJson;
    private final WriteObject<String> keyEncoder;
    private final WriteObject<Object> valueEncoder;
    private Map<String, Object> jsonElement;


    public TestDslJson() {
        obj = Foo.builder() //
                .intArrayValue(new int[] { 1, 2, 3, 4 }) //
                .doubleValue(0.123) //
                .barValue(Bar.builder() //
                        .b(true) //
                        .build()) //
                .intArrayList(CollectionUtils.newListBuilder(int[].class) //
                        .add(new int[] { 1, 2, 3 }) //
                        .add(new int[] { 5, 7, 6 }) //
                        .build()) //
                .longArrayMap(MapUtils.newMapStringKeyBuilder(long[].class) //
                        .put("longarr1", new long[] { 4l, 5l }) //
                        .put("longarr2", new long[] { 6l, 9l }) //
                        .build()) //
                .barMap((MapUtils.newMapStringKeyBuilder(Bar.class) //
                        .put("barEntry", Bar.builder() //
                                .b(true) //
                                .map(MapUtils.newMapStringKeyBuilder(Integer.class) //
                                        .put("key1", 10) //
                                        .build()) //
                                .build()) //
                        .build())) //
                .build();

        dslJson = new DslJson<Foo>();

        keyEncoder = (writer, key) -> {
            writer.writeString(key);
        };
        valueEncoder = (writer, value) -> {
            if (value == null) {
                writer.writeNull();
                return;
            }
            writer.writeString(value.toString());
        };

        jsonElement = PojoUtils.toJsonElement(obj);
    }

    @Test
    public void testDslMapToJson() {
        int round = (int) 1e7;
        long nanoTime = System.nanoTime();

        long start = nanoTime;
        for (int i = 0; i < round; i++) {
            JsonWriter writer = dslJson.newWriter();
            writer.serialize(jsonElement, keyEncoder, valueEncoder);
        }
        
        double seconds = Double.valueOf(System.nanoTime() - start) / 1e9;
        double pace = Double.valueOf(round) / seconds;

        System.out.println("dsl-json pace: " + new DecimalFormat("###,###.##").format(pace) + " ops/s");
    }

}
