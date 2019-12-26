package io.gridgo.utils.pojo.support;

import io.gridgo.utils.annotations.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransientVO {

    private boolean booleanValue;

    @Transient
    private boolean transientValue;
}
