package io.gridgo.utils.pojo.test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;

import io.gridgo.utils.pojo.getter.PojoGetter;
import io.gridgo.utils.pojo.setter.PojoSetter;
import io.gridgo.utils.pojo.setter.data.GenericData;
import io.gridgo.utils.pojo.setter.data.KeyValueData;
import io.gridgo.utils.pojo.setter.data.PrimitiveData;
import io.gridgo.utils.pojo.test.support.BooleanVO;

public class TestBooleanVO {

    private KeyValueData data;
    private Map<String, GenericData> map;

    @Before
    public void setUp() {
        map = new HashMap<String, GenericData>();
        data = new KeyValueData() {

            @Override
            public Iterator<Entry<String, GenericData>> iterator() {
                return map.entrySet().iterator();
            }

            @Override
            public GenericData getOrTake(String key, Supplier<GenericData> onAbsentSupplier) {
                if (map.containsKey(key))
                    return map.get(key);
                return onAbsentSupplier.get();
            }

            @Override
            public GenericData getOrDefault(String key, GenericData def) {
                return map.getOrDefault(key, def);
            }

            @Override
            public GenericData get(String key) {
                return map.get(key);
            }
        };
    }

    @Test
    @SuppressWarnings("incomplete-switch")
    public void testBooleanVO() {
        var vo = new BooleanVO();
        vo.setBooleanValue1(true);
        vo.setBooleanValue2(true);
        vo.setIsBooleanValue2(true);

        var key = new AtomicReference<String>(null);
        PojoGetter.of(vo).shallowly(true).walker((indicator, value, p) -> {
            switch (indicator) {
            case KEY:
                key.set((String) value);
                break;
            case VALUE:
                map.put(key.get(), new PrimitiveData() {
                    private final Object _value = value;

                    @Override
                    public Object getData() {
                        return _value;
                    }

                    @Override
                    public String toString() {
                        return this._value.toString();
                    }
                });
                break;
            }
        }).walk();

        var rebuiltVo = PojoSetter.ofType(BooleanVO.class).from(data).fill();

        assertEquals(vo, rebuiltVo);
    }
}
