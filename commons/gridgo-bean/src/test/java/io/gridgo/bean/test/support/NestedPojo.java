package io.gridgo.bean.test.support;

import lombok.Data;

@Data
public class NestedPojo {

    private NestedPojo child;
    
    private int id;
}
