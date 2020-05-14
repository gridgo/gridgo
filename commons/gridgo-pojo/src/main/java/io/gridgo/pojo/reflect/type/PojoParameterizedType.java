package io.gridgo.pojo.reflect.type;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Accessors(fluent = true)
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = true)
public class PojoParameterizedType extends PojoType {

    private final @NonNull Class<?> rawType;

    @Singular
    private final @NonNull List<PojoType> actualTypeArguments;

    @Override
    public String getSimpleName() {
        var sb = new StringBuilder();
        sb.append(rawType.getSimpleName());
        if (actualTypeArguments.size() > 0) {
            sb.append("<");
            sb.append(actualTypeArguments.get(0).getSimpleName());
            for (int i = 1; i < actualTypeArguments.size(); i++)
                sb.append(", ").append(actualTypeArguments.get(i).getSimpleName());
            sb.append(">");
        }
        return sb.toString();
    }

    @Override
    public boolean isParameterized() {
        return true;
    }

    @Override
    public String getSimpleNameWithoutGeneric() {
        var sb = new StringBuilder();
        sb.append(rawType.getSimpleName());
        return sb.toString();
    }
}
