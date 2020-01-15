package io.gridgo.bean.pojo;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BValue;
import io.gridgo.utils.pojo.setter.data.PrimitiveData;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class BPrimitiveData extends BGenericData implements PrimitiveData {

    @NonNull
    private final BValue value;

    @Override
    public Object getData() {
        return value.getData();
    }

    @Override
    public BElement getBElement() {
        return this.value;
    }
}
