package io.gridgo.otac;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacExceptionThrows implements OtacRequireImports {

    @Singular
    private final Set<Class<? extends Exception>> exceptions = new LinkedHashSet<>();

    @Override
    public Set<Class<?>> requiredImports() {
        return new HashSet<Class<?>>(exceptions);
    }

    @Override
    public String toString() {
        if (this.exceptions.isEmpty())
            return "";
        var sb = new StringBuilder();
        sb.append("throws ");
        var it = exceptions.iterator();
        var e = it.next();
        sb.append(e.getName());
        while (it.hasNext())
            sb.append(',').append(it.next().getName());
        return sb.toString();
    }
}
