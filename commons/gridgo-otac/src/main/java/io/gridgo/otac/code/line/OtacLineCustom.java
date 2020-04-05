package io.gridgo.otac.code.line;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacLineCustom extends OtacLine {

    public static final OtacLineCustom of(String content) {
        return OtacLineCustom.builder() //
                .content(content) //
                .build();
    }

    private @NonNull String content;

    @Override
    public String toStringWithoutSemicolon() {
        return content;
    }
}
