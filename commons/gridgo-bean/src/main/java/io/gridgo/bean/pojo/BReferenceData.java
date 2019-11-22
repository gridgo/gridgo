package io.gridgo.bean.pojo;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BReference;
import io.gridgo.utils.pojo.setter.data.ReferenceData;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class BReferenceData extends BGenericData implements ReferenceData {

    @NonNull
    private final BReference value;

    @Override
    public Object getReference() {
        return value.getReference();
    }
    
    @Override
    public BElement getBElement() {
        return this.value;
    }
}
