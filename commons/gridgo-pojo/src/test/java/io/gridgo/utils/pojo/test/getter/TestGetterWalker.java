package io.gridgo.utils.pojo.test.getter;

import static io.gridgo.utils.pojo.PojoFlattenIndicator.END_ARRAY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.END_MAP;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.KEY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.START_ARRAY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.START_MAP;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.VALUE;

import java.text.DecimalFormat;

import org.junit.Before;
import org.junit.Test;

import io.gridgo.utils.CollectionUtils;
import io.gridgo.utils.MapUtils;
import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;
import io.gridgo.utils.pojo.test.support.Bar;
import io.gridgo.utils.pojo.test.support.Foo;

public class TestGetterWalker {

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
                .build();

        // warm up
        PojoUtils.walkThroughGetter(obj, null, (indicator, vaue) -> {
            // do nothing
        });
    }

    private void walk(int indicator, Object value) {
//        AtomicInteger tab = new AtomicInteger(0);
        switch (indicator) {
        case START_MAP:
//            StringUtils.tabs(tab.get(), sb);
            // sb.append("{");
//            if ((int) value > 0) {
//                sb.append("\n");
//                tab.incrementAndGet();
//            }
            break;
        case KEY:
//            StringUtils.softTabs(tab.get(), sb);
            // sb.append(value.toString()).append(": ");
            break;
        case VALUE:
//            if (isNested.get()) {
//                StringUtils.softTabs(tab.get(), sb);
//            }
//            if (value != null && value.getClass().isArray()) {
//                value = ArrayUtils.toString(value);
//            }
            // sb.append(value == null ? "null" : value).append(", ");
            break;
        case END_MAP:
//            if ((int) value > 0) {
//                tab.decrementAndGet();
//            }
//            StringUtils.softTabs(tab.get(), sb);
            // sb.append("}");
            break;
        case START_ARRAY:
//            isNested.set(true);
            // sb.append("[");
//            if ((int) value > 0) {
//                sb.append("");
//                tab.incrementAndGet();
//            }
            break;
        case END_ARRAY:
//            isNested.set(false);
//            if ((int) value > 0) {
//                tab.decrementAndGet();
//            }
//            StringUtils.softTabs(tab.get(), sb);
            // sb.append("]");
            break;
        default:
            break;
        }
    }

    @Test
    public void testSimple() {

        PojoGetterProxy proxy = PojoUtils.getGetterProxy(Foo.class);
        int round = (int) 1e7;
        long nanoTime = System.nanoTime();

        long start = nanoTime;
        for (int i = 0; i < round; i++) {
            PojoUtils.walkThroughGetter(obj, proxy, this::walk);
        }
        double seconds = Double.valueOf(System.nanoTime() - start) / 1e9;
        double pace = Double.valueOf(round) / seconds;

        System.out.println("pace: " + new DecimalFormat("###,###.##").format(pace) + " ops/s");
    }
}
