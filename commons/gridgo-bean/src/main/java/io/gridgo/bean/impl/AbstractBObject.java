package io.gridgo.bean.impl;

import java.util.Map;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;

public abstract class AbstractBObject extends AbstractBContainer implements BObject {

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
