package io.gridgo.utils.pojo.support;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import io.gridgo.utils.pojo.FieldName;
import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.exception.PojoProxyException;
import io.gridgo.utils.pojo.setter.PojoSetter;
import lombok.Data;

public class TestException {

    @Data
    public static class InvalidTransformedFieldName {

        @FieldName("")
        private String field;
    }

    @Data
    public static class MissingNoArgsConstructor {

        @FieldName("")
        private String field;

        public MissingNoArgsConstructor(String arg) {
            this.field = arg;
        }
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidFieldNameException() {
        var proxy = PojoUtils.getGetterProxy(InvalidTransformedFieldName.class);
        assertNotNull(proxy);
    }

    @Test(expected = PojoProxyException.class)
    public void testCannotCreateInstance() {
        PojoSetter.ofType(MissingNoArgsConstructor.class);
    }
}
