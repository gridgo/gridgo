package io.gridgo.bean;

import static io.gridgo.bean.ImmutableBArray.UNSUPPORTED;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public interface WrappedBArray extends BArray {

    List<?> getSource();

    @Override
    default int size() {
        return getSource().size();
    }

    @Override
    default boolean isEmpty() {
        return getSource().isEmpty();
    }

    @Override
    default boolean contains(Object o) {
        return this.getSource().contains(o);
    }

    @Override
    default Iterator<BElement> iterator() {
        final Iterator<?> iterator = this.getSource().iterator();
        return new Iterator<BElement>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public BElement next() {
                return BElement.wrapAny(iterator.next());
            }
        };
    }

    @Override
    default Object[] toArray() {
        return this.getSource().toArray();
    }

    @Override
    default <T> T[] toArray(T[] a) {
        return this.getSource().toArray(a);
    }

    @Override
    default boolean containsAll(Collection<?> c) {
        return this.getSource().containsAll(c);
    }

    @Override
    default BElement get(int index) {
        return BElement.wrapAny(this.getSource().get(index));
    }

    @Override
    default int indexOf(Object o) {
        return this.getSource().indexOf(o);
    }

    @Override
    default int lastIndexOf(Object o) {
        return this.getSource().lastIndexOf(o);
    }

    @Override
    default ListIterator<BElement> listIterator() {
        return convertListIterator(this.getSource().listIterator());
    }

    @Override
    default ListIterator<BElement> listIterator(int index) {
        return this.convertListIterator(this.getSource().listIterator(index));
    }

    @Override
    default List<BElement> subList(int fromIndex, int toIndex) {
        return getFactory().wrap(this.getSource().subList(fromIndex, toIndex));
    }

    default ListIterator<BElement> convertListIterator(final ListIterator<?> listIterator) {
        return new ListIterator<BElement>() {

            @Override
            public boolean hasNext() {
                return listIterator.hasNext();
            }

            @Override
            public BElement next() {
                return BElement.wrapAny(listIterator.next());
            }

            @Override
            public boolean hasPrevious() {
                return listIterator.hasPrevious();
            }

            @Override
            public BElement previous() {
                return BElement.wrapAny(listIterator.previous());
            }

            @Override
            public int nextIndex() {
                return listIterator.nextIndex();
            }

            @Override
            public int previousIndex() {
                return listIterator.previousIndex();
            }

            @Override
            public void remove() {
                throw UNSUPPORTED;
            }

            @Override
            public void set(BElement e) {
                throw UNSUPPORTED;
            }

            @Override
            public void add(BElement e) {
                throw UNSUPPORTED;
            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    default List<Object> toList() {
        return (List<Object>) this.getSource();
    }
}
