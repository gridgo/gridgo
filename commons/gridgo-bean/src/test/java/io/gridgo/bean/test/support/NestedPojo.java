package io.gridgo.bean.test.support;

import com.dslplatform.json.CompiledJson;

import lombok.Data;

@Data
@CompiledJson
public class NestedPojo {

    private NestedPojo child;
    
    private int id;
}
