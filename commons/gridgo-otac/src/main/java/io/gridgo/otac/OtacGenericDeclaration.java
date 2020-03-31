package io.gridgo.otac;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacGenericDeclaration implements OtacRequireImports {

    @Builder.Default
    private boolean generic = false;

    @Singular
    private List<OtacGeneric> genericTypes;

    public boolean isGeneric() {
        return generic || (genericTypes != null && !genericTypes.isEmpty());
    }

    @Override
    public Set<Class<?>> requiredImports() {
        if (!isGeneric())
            return Collections.emptySet();
        var result = new HashSet<Class<?>>();
        if (genericTypes != null)
            for (var t : genericTypes)
                result.addAll(t.requiredImports());
        return result;
    }
}
