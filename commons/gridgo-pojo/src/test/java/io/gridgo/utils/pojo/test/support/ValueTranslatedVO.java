package io.gridgo.utils.pojo.test.support;

import io.gridgo.utils.pojo.translator.UseValueTranslator;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValueTranslatedVO {

    @UseValueTranslator("toString")
    private String translatedValue;
}
