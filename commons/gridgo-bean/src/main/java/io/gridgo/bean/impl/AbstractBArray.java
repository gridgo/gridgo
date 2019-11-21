package io.gridgo.bean.impl;

import io.gridgo.bean.BArray;
import io.gridgo.utils.ArrayUtils;

public abstract class AbstractBArray extends AbstractBContainer implements BArray {

    @Override
    public boolean equals(Object obj) {
        if (obj != null && ArrayUtils.isArrayOrCollection(obj.getClass())) {
            int size = this.size();
            if (ArrayUtils.length(obj) == size) {
                if (size > 0)
                    for (int i = 0; i < size; i++) {
                        if (!this.get(i).equals(ArrayUtils.entryAt(obj, i)))
                            return false;
                    }
                return true;
            }
        }
        return false;
    }
}
