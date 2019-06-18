package io.gridgo.bean.impl;

import java.util.Map;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.serialization.text.BPrinter;

@SuppressWarnings("unchecked")
public abstract class AbstractBObject extends AbstractBContainer implements BObject {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        BPrinter.print(sb, this);
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Map) {
            final Map<?, ?> map = (Map<?, ?>) obj;
            if (this.size() == map.size())
                for (Entry<String, BElement> entry : this.entrySet()) {
                    BElement myValue = entry.getValue();
                    Object otherValue = map.get(entry.getKey());
                    if (!myValue.equals(otherValue))
                        return false;
                }
            return true;
        }
        return false;
    }
}
