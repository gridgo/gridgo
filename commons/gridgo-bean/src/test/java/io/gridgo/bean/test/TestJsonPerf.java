package io.gridgo.bean.test;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;

import io.gridgo.bean.BObject;
import io.gridgo.bean.test.support.Bar;
import io.gridgo.bean.test.support.Foo;
import io.gridgo.utils.exception.RuntimeIOException;
import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;

@Slf4j
public class TestJsonPerf extends AbstractBenchmark {

    private Foo obj;
    private Map<String, Object> map;
    private PojoGetterProxy proxy;

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
                .build();
        map = BObject.ofPojo(obj).toMap();
        proxy = PojoUtils.getGetterProxy(Foo.class);
    }

    private static void benchmark(Runnable runner, int numRounds, String label) {
        var df = new DecimalFormat("###,###.##");

        long startTime = System.nanoTime();
        for (int i = 0; i < numRounds; i++) {
            runner.run();
        }
        long duration = System.nanoTime() - startTime;
        double seconds = duration * 1.0 / 1e9;
        log.debug("{} -> pace: {} ops/s", label, df.format(numRounds * 1.0 / seconds));
    }

    public static void main(String[] args) throws IOException {
        var obj = new TestJsonPerf();
        obj.setup();

        int numRounds = (int) 1e7;
        benchmark(obj::testPojoWalkThrough, numRounds, "pojo walkthrough");
        benchmark(obj::testJsonSmart, numRounds, "json smart");
    }

    @Test
    @BenchmarkOptions(concurrency = 1, warmupRounds = 1, benchmarkRounds = (int) 1e5)
    public void testPojoWalkThrough() {
        var sb = new StringBuilder();
        PojoUtils.walkThroughGetter(obj, proxy, (indicator, value) -> {
            switch (indicator) {
            case START_MAP:
                sb.append('{');
                break;
            case KEY:
                sb.append(value.toString()).append(':');
                break;
            case VALUE:
                sb.append(value == null ? "null" : value.toString()).append(',');
                break;
            case END_MAP:
                sb.append('}');
                break;
            case START_ARRAY:
                sb.append('[');
                break;
            case END_ARRAY:
                sb.append(']');
                break;
            default:
                break;
            }
        });
        sb.toString();
    }

    @Test
    @BenchmarkOptions(concurrency = 1, warmupRounds = 1, benchmarkRounds = (int) 1e5)
    public void testJsonSmart() {
        try {
            var sb = new StringBuilder();
            JSONObject.writeJSON(map, sb);
            sb.toString();
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }
}
