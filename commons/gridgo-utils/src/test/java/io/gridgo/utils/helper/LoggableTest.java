package io.gridgo.utils.helper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LoggableTest {


    class Implement implements Loggable {

    }

    @Test
    public void getLogger_withName() {
        var impl = new Implement();
        var ans = impl.getLogger(LoggableTest.class.getName());
        assertEquals(LoggableTest.class.getName(), ans.getName());
    }
}