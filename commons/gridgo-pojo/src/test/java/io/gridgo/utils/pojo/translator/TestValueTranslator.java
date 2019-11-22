package io.gridgo.utils.pojo.translator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import io.gridgo.utils.pojo.PojoMethodSignature;

public class TestValueTranslator {

    private static boolean methodCalled = false;
    private static boolean classCalled = false;

    @RegisterValueTranslator("dateToTimeStamp")
    public static long dateToTimestamp(Date date, PojoMethodSignature signature) {
        methodCalled = true;
        return date.getTime();
    }

    @RegisterValueTranslator("greeting")
    public static class MyTranslator implements ValueTranslator<String, String> {

        @Override
        public String translate(String obj, PojoMethodSignature signature) {
            classCalled = true;
            return "Hello " + obj;
        }
    }

    public static class CannotCreateInstance implements ValueTranslator<String, String> {

        private CannotCreateInstance() {
            // cannot create
        }

        @Override
        public String translate(String obj, PojoMethodSignature signature) {
            classCalled = true;
            return "Hello " + obj;
        }
    }

    @Before
    public void setup() {
        System.setProperty("gridgo.pojo.translator.scan", "io.gridgo.utils,");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRegisterValueTranslatorByMethod() {
        var translator = ValueTranslators.getInstance().lookupMandatory("dateToTimeStamp");
        assertTrue(translator instanceof MethodValueTranslator);

        assertFalse(translator.translatable(new Object()));

        var date = new Date();
        assertTrue(translator.translatable(date));

        long timestamp = date.getTime();
        assertEquals(timestamp, (long) translator.translate(date, null));

        assertTrue(methodCalled);

        var removed = ValueTranslators.getInstance().unregister("dateToTimeStamp");
        assertTrue(translator == removed);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRegisterValueTransformByClass() {
        var translator = ValueTranslators.getInstance().lookupMandatory("greeting");
        assertTrue(translator instanceof MyTranslator);

        assertTrue(translator.translatable(new Object()));

        var name = "John Smith";
        assertTrue(translator.translatable(name));

        assertEquals("Hello " + name, (String) translator.translate(name, null));

        assertTrue(classCalled);

        var removed = ValueTranslators.getInstance().unregister("greeting");
        assertTrue(translator == removed);
    }

    @Test(expected = RuntimeException.class)
    public void testLookupMandatory() {
        ValueTranslators.getInstance().lookupMandatory("notExist");
    }

    @Test(expected = RuntimeException.class)
    public void testRegisterValueTranslatorError() {
        ValueTranslators.getInstance().register("error", CannotCreateInstance.class);
    }

    @Test(expected = RuntimeException.class)
    public void testRegisterValueTranslatorNullKey() {
        ValueTranslators.getInstance().register(null, MyTranslator.class);
    }

    @Test(expected = RuntimeException.class)
    public void testRegisterValueTranslatorNullKey2() {
        ValueTranslators.getInstance().register(null, new MyTranslator());
    }

    @Test(expected = RuntimeException.class)
    public void testRegisterValueTranslatorNullValue() {
        ValueTranslators.getInstance().register("", (Class<?>) null);
    }

    @SuppressWarnings("rawtypes")
    @Test(expected = RuntimeException.class)
    public void testRegisterValueTranslatorNullValue2() {
        ValueTranslators.getInstance().register("", (ValueTranslator) null);
    }
}
