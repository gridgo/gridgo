package io.gridgo.utils.test;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

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
    }

    @Test
    public void testLowercaseFirst() {
        String text = "To-morrow, and to-morrow, and to-morrow";
        Assert.assertEquals("to-morrow, and to-morrow, and to-morrow", StringUtils.lowerCaseFirstLetter(text));
    }

    @Test
    public void testMatch() {
        String text = "You shall not pass";
        Assert.assertTrue(StringUtils.match(text, "You.*shall.*pass"));
    }

    @Test
    public void testImplode() {
        String text = "one string to rule them all";
        Assert.assertEquals(text, StringUtils.implode("one ", "string ", "to ", "rule ", "them ", "all"));
    }

    @Test
    public void testImplodeWithGlue() {
        String text = "one string to bind them all";
        Assert.assertEquals(text, StringUtils.implodeWithGlue(" ", "one", "string", "to", "bind", "them", "all"));
        Assert.assertEquals(text,
                StringUtils.implodeWithGlue(" ", Arrays.asList("one", "string", "to", "bind", "them", "all")));
    }

    @Test
    public void testTab() {
        Assert.assertEquals("\t\t\t\t", StringUtils.tabs(4));
    }

    @Test
    public void testIsNumber() {
        Assert.assertTrue(StringUtils.isRepresentNumber("5"));
        Assert.assertTrue(StringUtils.isRepresentNumber("5.0"));
        Assert.assertTrue(StringUtils.isRepresentNumber("-5"));
        Assert.assertTrue(StringUtils.isRepresentNumber("-5.0"));
        Assert.assertFalse(StringUtils.isRepresentNumber("-5.0."));
        Assert.assertFalse(StringUtils.isRepresentNumber("-5.."));
        Assert.assertFalse(StringUtils.isRepresentNumber("-5.0.0"));
        Assert.assertFalse(StringUtils.isRepresentNumber("5-0"));
        Assert.assertFalse(StringUtils.isRepresentNumber("-5a"));
        Assert.assertFalse(StringUtils.isRepresentNumber("a"));
    }
}
