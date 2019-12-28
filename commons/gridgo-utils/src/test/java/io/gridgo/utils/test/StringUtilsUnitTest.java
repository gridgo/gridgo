package io.gridgo.utils.test;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import io.gridgo.utils.StringUtils;
import io.gridgo.utils.exception.RuntimeIOException;

public class StringUtilsUnitTest {

    @Test
    public void testGetAllMatches() {
        String text = "foul is fair and fair is foul";
        String pattern = "f....is";
        var matches = StringUtils.getAllMatches(text, pattern);
        Assert.assertEquals(2, matches.size());
        Assert.assertEquals("foul is", matches.get(0));
        Assert.assertEquals("fair is", matches.get(1));
    }

    @Test
    public void testUppercaseFirst() {
        String text = "to-morrow, and to-morrow, and to-morrow";
        Assert.assertEquals("To-morrow, and to-morrow, and to-morrow", StringUtils.upperCaseFirstLetter(text));
        Assert.assertEquals("", StringUtils.upperCaseFirstLetter(""));
        Assert.assertEquals(" t", StringUtils.upperCaseFirstLetter(" t"));
        Assert.assertEquals("T", StringUtils.upperCaseFirstLetter("t"));
        Assert.assertNull(StringUtils.upperCaseFirstLetter(null));
    }

    @Test
    public void testLowercaseFirst() {
        String text = "To-morrow, and to-morrow, and to-morrow";
        Assert.assertEquals("to-morrow, and to-morrow, and to-morrow", StringUtils.lowerCaseFirstLetter(text));
        Assert.assertEquals("", StringUtils.lowerCaseFirstLetter(""));
        Assert.assertEquals(" T", StringUtils.lowerCaseFirstLetter(" T"));
        Assert.assertEquals("t", StringUtils.lowerCaseFirstLetter("T"));
        Assert.assertNull(StringUtils.lowerCaseFirstLetter(null));
    }

    @Test
    public void testImplodeWithGlue() {
        String text = "one string to bind them all";
        Assert.assertEquals(text,
                StringUtils.implodeWithGlue(" ", Arrays.asList("one", "string", "to", "bind", "them", "all")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testImplodeWithGlueStartAfterEnd() {
        StringUtils.implodeWithGlue(" ", new String[] { "one", "string"}, 2, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testImplodeWithGlueStartAfterLength() {
        StringUtils.implodeWithGlue(" ", new String[] { "one", "string"}, 2, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testImplodeWithGlueEndAfterLength() {
        StringUtils.implodeWithGlue(" ", new String[] { "one", "string"}, 0, 3);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testImplodeWithGlueNegativeStart() {
        StringUtils.implodeWithGlue(" ", new String[] { "one", "string"}, -1, 2);
    }

    @Test
    public void testImplodeWithGlueWithBound() {
        String text = "string to bind them";
        String[] array = new String[] { "one", "string", "to", "bind", "them", "all" };
        Assert.assertEquals(text, StringUtils.implodeWithGlue(" ", array, 1, array.length - 1));
    }

    @Test(expected = RuntimeIOException.class)
    public void testTabWithException() {
        StringUtils.tabs(1, new Appendable() {

            @Override
            public Appendable append(CharSequence csq, int start, int end) throws IOException {
                throw new IOException();
            }

            @Override
            public Appendable append(char c) throws IOException {
                throw new IOException();
            }

            @Override
            public Appendable append(CharSequence csq) throws IOException {
                throw new IOException();
            }
        });
    }

    @Test
    public void testTab() {
        var sb = new StringBuilder();
        StringUtils.tabs(1, sb);
        Assert.assertEquals("\t", sb.toString());

        sb = new StringBuilder();
        StringUtils.tabs(2, sb);
        Assert.assertEquals("\t\t", sb.toString());
    }
}
