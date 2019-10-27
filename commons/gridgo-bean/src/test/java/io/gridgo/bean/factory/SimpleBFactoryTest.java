package io.gridgo.bean.factory;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class SimpleBFactoryTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void setValueSupplier() {
        SimpleBFactory simpleBFactory = new SimpleBFactory();
        var supplier = mock(Supplier.class);
        simpleBFactory.setValueSupplier(supplier);
        assertSame(supplier, simpleBFactory.getValueSupplier());
    }

    @Test
    public void setObjectSupplier() {
        SimpleBFactory simpleBFactory = new SimpleBFactory();
        var input = mock(Function.class);
        simpleBFactory.setObjectSupplier(input);
        assertSame(input, simpleBFactory.getObjectSupplier());
    }
}