package io.gridgo.pojo;

import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Getter
@Builder
public class PojoSchemaConfig {

    public static final PojoSchemaConfig DEFAULT = PojoSchemaConfig.builder().includeDefault(true).build();

    private final boolean includeDefault;

    @Singular
    private final Set<String> includeTags;
}
