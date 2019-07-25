package io.gridgo.utils.pojo.setter;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ValueHolder {

    public static final ValueHolder NO_VALUE = new ValueHolder();

    @Getter
    private Object value = null;

    private boolean hasValue = false;

    public ValueHolder(Object value) {
        this.setValue(value);
    }

    public void reset() {
        this.hasValue = false;
        this.value = null;
    }

    public void setValue(Object value) {
        this.value = value;
        this.hasValue = true;
    }

    public boolean hasValue() {
        return this.hasValue;
    }
}
