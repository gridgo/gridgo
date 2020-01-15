package io.gridgo.utils.pojo.getter;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.gridgo.utils.pojo.PojoFlattenIndicator;
import io.gridgo.utils.pojo.getter.fieldwalkers.GenericFieldWalker;

public class TestGenericFieldWalker {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testWalkShallow() {
        var result = new HashMap<PojoFlattenIndicator, List>();
        var list = new Object[] { 1, Map.of("k2", 2), List.of(3, 4), new int[] { 5 } };
        var fieldWalker = GenericFieldWalker.getInstance();
        fieldWalker.walk(list, null, (indicator, value, signature, proxy) -> {
            result.computeIfAbsent(indicator, k -> new ArrayList<>())
                  .add(value);
        }, true);
        Assert.assertNull(result.get(PojoFlattenIndicator.START_MAP));
        Assert.assertNull(result.get(PojoFlattenIndicator.KEY));
        Assert.assertNull(result.get(PojoFlattenIndicator.END_MAP));
        Assert.assertEquals(List.of(4), result.get(PojoFlattenIndicator.START_ARRAY));
        Assert.assertEquals(List.of(4), result.get(PojoFlattenIndicator.END_ARRAY));
        var values = result.get(PojoFlattenIndicator.VALUE);
        Assert.assertEquals(Set.of(list), Set.copyOf(values));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testWalkDeep() {
        var result = new HashMap<PojoFlattenIndicator, List>();
        var list = new Object[] { 1, Map.of("k2", 2), List.of(3, 4), new int[] { 5 } };
        var fieldWalker = GenericFieldWalker.getInstance();
        fieldWalker.walk(list, null, (indicator, value, signature, proxy) -> {
            result.computeIfAbsent(indicator, k -> new ArrayList<>())
                  .add(value);
        }, false);
        Assert.assertEquals(List.of(1), result.get(PojoFlattenIndicator.START_MAP));
        Assert.assertEquals(List.of("k2"), result.get(PojoFlattenIndicator.KEY));
        Assert.assertEquals(List.of(1), result.get(PojoFlattenIndicator.END_MAP));
        Assert.assertEquals(Set.of(1, 2, 4), Set.copyOf(result.get(PojoFlattenIndicator.START_ARRAY)));
        Assert.assertEquals(Set.of(1, 2, 4), Set.copyOf(result.get(PojoFlattenIndicator.END_ARRAY)));
        var values = result.get(PojoFlattenIndicator.VALUE);
        Assert.assertEquals(Set.of(1, 2, 3, 4, 5), Set.copyOf(values));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testWalkNull() {
        var result = new HashMap<PojoFlattenIndicator, List>();
        var fieldWalker = GenericFieldWalker.getInstance();
        fieldWalker.walk(null, null, (indicator, value, signature, proxy) -> {
            result.computeIfAbsent(indicator, k -> new ArrayList<>())
                  .add(value);
        }, false);
        Assert.assertNull(result.get(PojoFlattenIndicator.START_MAP));
        Assert.assertNull(result.get(PojoFlattenIndicator.KEY));
        Assert.assertNull(result.get(PojoFlattenIndicator.END_MAP));
        Assert.assertNull(result.get(PojoFlattenIndicator.START_ARRAY));
        Assert.assertNull(result.get(PojoFlattenIndicator.END_ARRAY));
        var values = result.get(PojoFlattenIndicator.VALUE);
        Assert.assertEquals(Arrays.asList(new Object[] { null }), values);
    }
}
