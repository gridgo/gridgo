package io.gridgo.utils.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import io.gridgo.utils.StringUtils;

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

    @Test
    public void testImplodeWithGlueWithBound() {
        String text = "string to bind them";
        String[] array = new String[] { "one", "string", "to", "bind", "them", "all" };
        Assert.assertEquals(text, StringUtils.implodeWithGlue(" ", array, 1, array.length - 1));
    }
}
