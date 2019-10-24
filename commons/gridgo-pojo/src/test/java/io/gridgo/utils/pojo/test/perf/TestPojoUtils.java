package io.gridgo.utils.pojo.test.perf;

import static io.gridgo.utils.pojo.PojoFlattenIndicator.END_ARRAY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.END_MAP;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.KEY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.START_ARRAY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.START_MAP;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.VALUE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.gridgo.utils.CollectionUtils;
import io.gridgo.utils.MapUtils;
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
                .intArrayList(CollectionUtils.newListBuilder(int[].class) //
                        .add(new int[] { 1, 2, 3 }) //
                        .add(new int[] { 5, 7, 6 }) //
                        .build()) //
                .longArrayMap(MapUtils.newMapStringKeyBuilder(long[].class) //
                        .put("longarr1", new long[] { 4l, 5l }) //
                        .put("longarr1", new long[] { 6l, 9l }) //
                        .build()) //
                .barMap((MapUtils.newMapStringKeyBuilder(Bar.class) //
                        .put("barEntry", Bar.builder() //
                                .b(true) //
                                .map(MapUtils.newMapStringKeyBuilder(Integer.class) //
                                        .put("key1", 10) //
                                        .build()) //
                                .build()) //
                        .build())) //
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
        int round = (int) 1;
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
