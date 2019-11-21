package io.gridgo.utils.pojo.test.support;

import io.gridgo.utils.pojo.translator.UseValueTranslator;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValueTranslatedVO {

    @UseValueTranslator("toString")
    private String translatedValue;

    private String translatedValue2;

    @UseValueTranslator("toString")
    public void setTranslatedValue2(String value) {
        this.translatedValue2 = value;
    }

    public void setTranslatedValue3(String value) {

    }
}
