package io.gridgo.framework.execution.impl;

import io.gridgo.framework.support.context.ExecutionContext;
import lombok.Data;

@Data
public class ExecutionContextEvent<T, H> {

    private ExecutionContext<T, H> context;

    public void clear() {
        this.context = null;
    }
}
