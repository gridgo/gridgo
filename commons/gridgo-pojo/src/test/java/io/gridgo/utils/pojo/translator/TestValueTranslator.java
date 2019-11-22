package io.gridgo.utils.pojo.translator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

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

    @Test
    @SuppressWarnings("unchecked")
    public void testRegisterValueTranslatorByMethod() {
        var translator = ValueTranslators.getInstance().lookup("dateToTimeStamp");
        assertTrue(translator instanceof MethodValueTranslator);

        assertFalse(translator.translatable(new Object()));

        var date = new Date();
        assertTrue(translator.translatable(date));

        long timestamp = date.getTime();
        assertEquals(timestamp, (long) translator.translate(date, null));

        assertTrue(methodCalled);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRegisterValueTransformByClass() {
        var translator = ValueTranslators.getInstance().lookup("greeting");
        assertTrue(translator instanceof MyTranslator);

        assertTrue(translator.translatable(new Object()));

        var name = "John Smith";
        assertTrue(translator.translatable(name));

        assertEquals("Hello " + name, (String) translator.translate(name, null));

        assertTrue(classCalled);
    }
}
