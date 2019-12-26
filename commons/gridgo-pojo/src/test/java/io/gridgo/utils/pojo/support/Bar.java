package io.gridgo.utils.pojo.support;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bar {

    private boolean b;

    private Map<String, Integer> map;

    private Map<String, Object> genericMap;
}
