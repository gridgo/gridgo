package io.gridgo.framework.test.support;

import io.gridgo.framework.impl.AbstractComponentLifecycle;
import lombok.Getter;

public class TestComponent extends AbstractComponentLifecycle {

    @Getter
    private int data = 0;

    @Override
    protected String generateName() {
        return "test";
    }

    @Override
    protected void onStart() {
        data++;
    }

    @Override
    protected void onStop() {
        data--;
    }

}
