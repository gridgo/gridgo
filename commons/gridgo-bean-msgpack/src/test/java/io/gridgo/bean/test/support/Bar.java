package io.gridgo.bean.test.support;

import java.util.Map;

import io.gridgo.bean.impl.BReferenceBeautifulPrint;
import io.gridgo.utils.annotations.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@BReferenceBeautifulPrint
public class Bar {

    private Boolean bool;

    private Map<String, Integer> map;

    @Transient
    public boolean isBool() {
        return bool == null ? false : bool.booleanValue();
    }
}
