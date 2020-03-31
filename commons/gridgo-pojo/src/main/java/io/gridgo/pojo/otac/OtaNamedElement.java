package io.gridgo.pojo.otac;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtaNamedElement extends OtacModifiers {

    private @NonNull String name;

}
