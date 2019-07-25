package io.gridgo.utils.pojo.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.test.support.AbstractTest;
import io.gridgo.utils.pojo.test.support.CollectionVO;
import io.gridgo.utils.pojo.test.support.PrimitiveVO;

public class TestPojoGetterCollection extends AbstractTest {

    private CollectionVO target = new CollectionVO();

    @Test
    public void testList() {
        String fieldName = "listPrimitive";
        List<PrimitiveVO> value = new LinkedList<>(Arrays.asList(new PrimitiveVO()));

        target.setListPrimitive(value);
        assertEquals(value, PojoUtils.getValue(target, fieldName));
    }

    @Test
    public void testSet() {
        String fieldName = "setPrimitive";
        Set<PrimitiveVO> value = new HashSet<>(Arrays.asList(new PrimitiveVO()));

        target.setSetPrimitive(value);
        assertEquals(value, PojoUtils.getValue(target, fieldName));
    }

    @Test
    public void testMap() {
        String fieldName = "mapPrimitive";
        Map<String, PrimitiveVO> value = new HashMap<>();

        target.setMapPrimitive(value);
        assertEquals(value, PojoUtils.getValue(target, fieldName));
    }
}
