package io.gridgo.bean.test.support;

import java.util.Map;

import io.gridgo.bean.impl.BReferenceBeautifulPrint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@BReferenceBeautifulPrint
public class Bar {

    private boolean b;

    private Map<String, Integer> map;
}
