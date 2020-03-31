package io.gridgo.pojo.otac;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import io.gridgo.otac.OtacGeneric;
import io.gridgo.otac.OtacInheritOperator;
import io.gridgo.otac.OtacType;

public class TestOtacGeneric {

    @Test
    public void testToString() {
        var g = OtacGeneric.builder() //
                .name("T") //
                .operator(OtacInheritOperator.SUPER) //
                .type(OtacType.of(List.class)) //
                .build();

        assertEquals("T super List", g.toString().trim());
        assertTrue(g.requiredImports().contains(List.class));
    }
}
