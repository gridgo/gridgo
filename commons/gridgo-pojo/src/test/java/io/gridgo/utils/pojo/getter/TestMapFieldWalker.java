package io.gridgo.utils.pojo.getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.utils.pojo.PojoFlattenIndicator;
import io.gridgo.utils.pojo.getter.fieldwalkers.MapFieldWalker;

public class TestMapFieldWalker {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testWalkShallow() {
        var result = new HashMap<PojoFlattenIndicator, List>();
        var map = Map.of("k1", 1, "k2", Map.of("k2.1", 2.1));
        var fieldWalker = MapFieldWalker.getInstance();
        fieldWalker.walk(map, null, (indicator, value, signature, proxy) -> {
            result.computeIfAbsent(indicator, k -> new ArrayList<>())
                  .add(value);
        }, true);
        Assert.assertEquals(List.of(2), result.get(PojoFlattenIndicator.START_MAP));
        Assert.assertEquals(List.of(2), result.get(PojoFlattenIndicator.END_MAP));
        var keys = result.get(PojoFlattenIndicator.KEY);
        Assert.assertEquals(Set.of("k1", "k2"), Set.copyOf(keys));
        var values = result.get(PojoFlattenIndicator.VALUE);
        Assert.assertEquals(Set.of(map.get("k2"), 1), Set.copyOf(values));
    }

    @Test
    public void testWalkDeep() {
        var result = new HashMap<PojoFlattenIndicator, List<Object>>();
        var map = Map.of("k1", 1, "k2", Map.of("k2.1", 2.1));
        var fieldWalker = MapFieldWalker.getInstance();
        fieldWalker.walk(map, null, (indicator, value, signature, proxy) -> {
            result.computeIfAbsent(indicator, k -> new ArrayList<>())
                .add(value);
        }, false);
        Assert.assertEquals(Set.of(1, 2), Set.copyOf(result.get(PojoFlattenIndicator.START_MAP)));
        Assert.assertEquals(Set.of(1, 2), Set.copyOf(result.get(PojoFlattenIndicator.END_MAP)));
        Assert.assertEquals(Set.of("k1", "k2", "k2.1"), Set.copyOf(result.get(PojoFlattenIndicator.KEY)));
        Assert.assertEquals(Set.of(1, 2.1), Set.copyOf(result.get(PojoFlattenIndicator.VALUE)));
    }
}
