package io.gridgo.utils.pojo.test.perf;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import static io.gridgo.utils.pojo.PojoFlattenIndicator.END_ARRAY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.END_MAP;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.KEY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.START_ARRAY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.START_MAP;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.VALUE;

import io.gridgo.utils.exception.RuntimeIOException;
import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;
import io.gridgo.utils.pojo.test.support.Bar;
import io.gridgo.utils.pojo.test.support.Foo;

public class TestPojoUtils {

    private Foo obj;

    @Before
    public void setup() {
        obj = Foo.builder() //
                .intArrayValue(new int[] { 1, 2, 3, 4 }) //
                .doubleValue(0.123) //
                .barValue(Bar.builder() //
                        .b(true) //
                        .build()) //
                .intArrayList(Arrays.asList( //
                        new int[] { 1, 2, 3 }, //
                        new int[] { 5, 7, 6 })) //
                .longArrayMap(Map.of( //
                        "longarr1", new long[] { 4l, 5l }, //
                        "longarr2", new long[] { 6l, 9l })) //
                .barMap(Map.of( //
                        "barEntry", Bar.builder() //
                                .b(true) //
                                .map(Map.of("key1", 10)).build() //
                )) //
                .date(new Date()) //
                .build();

        // warm up
        PojoUtils.walkThroughGetter(obj, null, (indicator, vaue) -> {
            // do nothing
        });
    }

    private void walk(int indicator, Object value) {
        try (var output = new ByteArrayOutputStream(512)) {
            var sb = new OutputStreamWriter(output);
            switch (indicator) {
            case START_MAP:
                sb.append("{");
                break;
            case KEY:
                sb.append(String.valueOf(value)).append(":");
                break;
            case VALUE:
                sb.append(value == null ? "null" : String.valueOf(value)).append(",");
                break;
            case END_MAP:
                sb.append("}");
                break;
            case START_ARRAY:
                sb.append("[");
                break;
            case END_ARRAY:
                sb.append("]");
                break;
            default:
                break;
            }
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    @Test
    public void testPojoUnsupported() {
        var signatures = PojoUtils.extractGetterMethodSignatures(Foo.class);
        Assert.assertFalse(signatures.isEmpty());
        signatures = PojoUtils.extractGetterMethodSignatures(Date.class);
        Assert.assertTrue(signatures.isEmpty());
    }

    @Test
    public void testPojoUtils() {
        PojoGetterProxy proxy = PojoUtils.getGetterProxy(Foo.class);
        int round = 1;
        long nanoTime = System.nanoTime();

        long start = nanoTime;
        for (int i = 0; i < round; i++) {
            PojoUtils.walkThroughGetter(obj, proxy, this::walk);
        }
        double seconds = Double.valueOf(System.nanoTime() - start) / 1e9;
        double pace = Double.valueOf(round) / seconds;

        System.out.println("pojo-utils pace: " + new DecimalFormat("###,###.##").format(pace) + " ops/s");
    }
}
