package io.gridgo.utils.pojo.test.perf;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import io.gridgo.utils.pojo.PojoFlattenIndicator;
import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.getter.GetterMethodSignatureExtractor;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;
import io.gridgo.utils.pojo.setter.SetterMethodSignatureExtractor;
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
    }

    private void walk(PojoFlattenIndicator indicator, Object value) {
        var sb = new StringBuilder();
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
    }

    @Test
    public void testPojoUnsupported() {
        var signatures = GetterMethodSignatureExtractor.getInstance().extractMethodSignatures(Foo.class);
        Assert.assertFalse(signatures.isEmpty());
        signatures = SetterMethodSignatureExtractor.getInstance().extractMethodSignatures(Date.class);
        Assert.assertTrue(signatures.isEmpty());
    }

    @Test
    public void testPojoUtils() {
        PojoGetterProxy proxy = PojoUtils.getGetterProxy(Foo.class);
        PojoUtils.walkThroughGetter(obj, proxy, this::walk);
    }
}
