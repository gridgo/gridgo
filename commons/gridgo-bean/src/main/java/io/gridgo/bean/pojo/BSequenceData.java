package io.gridgo.bean.pojo;

import java.util.Iterator;

import io.gridgo.bean.BArray;
import io.gridgo.utils.pojo.setter.data.GenericData;
import io.gridgo.utils.pojo.setter.data.SequenceData;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class BSequenceData extends BGenericData implements SequenceData {

    @NonNull
    private final BArray array;

    @Override
    public Iterator<GenericData> iterator() {
        var it = array.iterator();
        return new Iterator<GenericData>() {

            @Override
            public GenericData next() {
                return BGenericData.ofAny(it.next());
            }

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }
        };
    }

    @Override
    public GenericData get(int index) {
        return BGenericData.ofAny(array.get(index));
    }
}
