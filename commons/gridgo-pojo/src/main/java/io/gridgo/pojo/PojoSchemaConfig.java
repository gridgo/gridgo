package io.gridgo.pojo;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class PojoSchemaConfig {

    public static final PojoSchemaConfig DEFAULT = PojoSchemaConfig.builder().includeDefault(true).build();

    public static PojoSchemaConfig includeDefault() {
        return DEFAULT;
    }

    public static PojoSchemaConfig includeDefaultAndTags(Collection<String> tags) {
        return PojoSchemaConfig.builder() //
                .includeDefault(true) //
                .includeTags(tags) //
                .build();
    }

    public static PojoSchemaConfig includeDefaultAndTags(String... tags) {
        return PojoSchemaConfig.includeDefaultAndTags(Arrays.asList(tags));
    }

    public static PojoSchemaConfig includeTags(Collection<String> tags) {
        return PojoSchemaConfig.builder() //
                .includeDefault(false) //
                .includeTags(tags) //
                .build();
    }

    public static PojoSchemaConfig includeTags(String... tags) {
        return includeTags(Arrays.asList(tags));
    }

    private final boolean includeDefault;

    @Singular
    private final Set<String> includeTags;
}
