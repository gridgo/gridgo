package io.gridgo.bean.test;

import java.util.TreeSet;

import org.junit.Ignore;
import org.junit.Test;

public class TestTreeSet {

    @Test
    @Ignore
    public void testAddNull() {
        var set = new TreeSet<Object>();
        set.add(null);
    }
}
