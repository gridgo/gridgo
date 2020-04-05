package io.gridgo.utils.pojo;

import static java.util.Collections.unmodifiableList;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NonNull;

public abstract class AbstractProxy implements PojoProxy {

    public static final String SIGNATURE_FIELD_SUBFIX = "Signature";

    @Getter
    private @NonNull final String[] fields;

    @Getter
    private final List<PojoMethodSignature> signatures;

    protected AbstractProxy(List<PojoMethodSignature> signatures) {
        this.signatures = unmodifiableList(signatures);
        this.fields = signatures.stream() //
                .map(PojoMethodSignature::getFieldName) //
                .collect(Collectors.toList()) //
                .toArray(String[]::new);
        for (var sig : signatures) {
            try {
                var field = this.getClass().getDeclaredField(sig.getFieldName() + SIGNATURE_FIELD_SUBFIX);
                if (field.trySetAccessible())
                    field.set(this, sig);
            } catch (Exception e) {
                // do nothing
            }
        }
    }
}
