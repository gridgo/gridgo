package io.gridgo.bean.test.support;

import io.gridgo.bean.impl.BReferenceBeautifulPrint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@BReferenceBeautifulPrint
public class DslJsonUnsupportedPojo {

    private boolean booleanVal;
    private byte byteVal;
}
