package io.gridgo.otac;

import lombok.Getter;
import lombok.Setter;

public interface OtacDeclaringClassAware {

    void setDeclaringClass(OtacClass declaringClass);

    OtacClass getDeclaringClass();

    static OtacDeclaringClassAware newInstance() {
        return new OtacDeclaringClassAware() {
            @Setter
            @Getter
            private OtacClass declaringClass;
        };
    }
}
