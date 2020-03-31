package io.gridgo.otac;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

@Getter
@Builder
public class OtacAnnotation implements OtacRequireImports {

    private @NonNull Class<? extends Annotation> type;

    @Singular
    private Map<String, OtacType> metadatas;

    public static OtacAnnotation annotation(Class<? extends Annotation> annotation) {
        return OtacAnnotation.builder().type(annotation).build();
    }

    @Override
    public Set<Class<?>> requiredImports() {
        var imports = new HashSet<Class<?>>();
        imports.add(type);
        for (var m : metadatas.values())
            imports.addAll(m.requiredImports());
        return imports;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("@").append(type.getSimpleName());
        if (metadatas != null && metadatas.size() > 0) {
            sb.append("(");
            var it = metadatas.entrySet().iterator();
            var entry = it.next();
            sb.append(entry.getKey()).append(" = ").append(entry.getValue().toString().trim());
            while (it.hasNext())
                sb.append(", ").append(entry.getKey()).append(" = ").append(entry.getValue().toString().trim());
            sb.append(")");
        }
        return sb.toString();
    }
}
