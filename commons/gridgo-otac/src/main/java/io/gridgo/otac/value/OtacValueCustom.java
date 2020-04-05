package io.gridgo.otac.value;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacValueCustom extends OtacValue {

    private @NonNull String content;

    @Override
    public String toString() {
        return content;
    }
}
