package io.gridgo.utils.helper;

import org.junit.Test;

import static org.junit.Assert.*;

public class CastShortcutTest {

    class Father implements CastShortcut<Father> {

    }

    class Son extends Father {

    }

    @Test
    public void castTo() {
        Father son = new Son();
        var casted = son.castTo(Son.class);
        assertNotNull(casted);
    }
}