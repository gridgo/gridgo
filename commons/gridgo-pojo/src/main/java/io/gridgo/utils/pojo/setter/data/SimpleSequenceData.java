package io.gridgo.utils.pojo.setter.data;

import java.util.Collection;
import java.util.Iterator;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class SimpleSequenceData extends SimpleGenericData implements SequenceData {

    @NonNull
    private final Collection<?> list;

    @AllArgsConstructor
    private static class WrappedIterator implements Iterator<GenericData> {
        @NonNull
        private final Iterator<?> it;

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public GenericData next() {
            return SimpleGenericData.of(it.next());
        }
    }

    @Override
    public Iterator<GenericData> iterator() {
        return new WrappedIterator(this.list.iterator());
    }
}
