package io.gridgo.config.test;

import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.ContextAwareComponent;
import lombok.Setter;

public class TestComponent implements ContextAwareComponent {

    @Setter
    private GridgoContext context;

    @Override
    public String getName() {
        return "test";
    }
}
