package io.gridgo.utils.pojo.test;

import org.junit.Test;

import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.test.support.BooleanVO;

public class TestBooleanField {

    @Test
    public void testBooleanField1() {
        BooleanVO vo = new BooleanVO();
        var jsonElement = PojoUtils.toJsonElement(vo);
        System.out.println(jsonElement.toString());
    }
}
