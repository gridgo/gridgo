package io.gridgo.utils.test;

import static io.gridgo.utils.ClasspathUtils.scanForAnnotatedFields;
import static io.gridgo.utils.ClasspathUtils.scanForAnnotatedMethods;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import io.gridgo.utils.test.support.TestFieldAnnotation;
import io.gridgo.utils.test.support.TestMethodAnnotation;

public class ClasspathUtilsTest {

    @TestFieldAnnotation("test-value")
    public String testText = "This is test text";

    @TestMethodAnnotation
    public String testReturnString(String testString) {
        return testString;
    }

    @Test
    public void testMethodAnnotationScanner() {
        var testText = "This is test text";
        var resultText = new AtomicReference<String>(null);
        scanForAnnotatedMethods(//
                getClass().getPackageName(), //
                TestMethodAnnotation.class, //
                (method, annotation) -> {
                    if (method.getDeclaringClass() == ClasspathUtilsTest.class
                            && "testReturnString".equals(method.getName())) {
                        try {
                            resultText.set((String) method.invoke(this, testText));
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

        assertEquals(testText, resultText.get());
    }

    @Test
    public void testFieldAnnotationScanner() {
        var testText = "This is test text";
        var resultText = new AtomicReference<String>(null);
        var resultAnnotationValue = new AtomicReference<String>(null);
        scanForAnnotatedFields(//
                getClass().getPackageName(), //
                TestFieldAnnotation.class, //
                (field, annotation) -> {
                    if (field.getDeclaringClass() == ClasspathUtilsTest.class && "testText".equals(field.getName())) {
                        try {
                            resultText.set((String) field.get(this));
                            resultAnnotationValue.set(annotation.value());
                        } catch (IllegalAccessException | IllegalArgumentException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

        assertEquals(testText, resultText.get());
        assertEquals("test-value", resultAnnotationValue.get());
    }
}
