package io.gridgo.bean.test.support;

import static lombok.AccessLevel.PRIVATE;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BValue;
import io.gridgo.utils.annotations.Transient;
import io.gridgo.utils.pojo.translator.OnSetTranslate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class PojoWithBElement {

    @OnSetTranslate("toBValue")
    private BValue bValue;

    @OnSetTranslate("toBObject")
    private BObject bObject;

    @OnSetTranslate("toBArray")
    private BArray bArray;

    @Transient
    private transient BElement myBElement;

    @OnSetTranslate("toBElement")
    public void setBElement(BElement belement) {
        this.myBElement = belement;
    }

    public BElement getBElement() {
        return this.myBElement;
    }
}
