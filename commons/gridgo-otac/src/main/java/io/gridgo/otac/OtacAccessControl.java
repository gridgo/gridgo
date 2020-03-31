package io.gridgo.otac;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacAccessControl {

    @Builder.Default
    private @NonNull OtacAccessLevel accessLevel = OtacAccessLevel.PACKAGE;

    @Override
    public String toString() {
        return accessLevel.getKeyword();
    }
}
