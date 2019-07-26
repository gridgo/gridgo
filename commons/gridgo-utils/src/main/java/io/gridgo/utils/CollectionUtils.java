package io.gridgo.utils;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

public class CollectionUtils {

    @AllArgsConstructor(access = PRIVATE)
    private static class CollectionBuilder<E> {

        @Getter(PROTECTED)
        private final @NonNull Collection<E> collection;
    }

    public static final class ListBuilder<E> extends CollectionBuilder<E> {

        private ListBuilder(List<E> list) {
            super(list);
        }

        public ListBuilder<E> remove(int index) {
            ((List<E>) this.getCollection()).remove(index);
            return this;
        }

        public final ListBuilder<E> add(E element) {
            this.getCollection().add(element);
            return this;
        }

        public final ListBuilder<E> addAll(Collection<E> collection) {
            this.getCollection().addAll(collection);
            return this;
        }

        public final ListBuilder<E> remove(Object object) {
            this.getCollection().remove(object);
            return this;
        }

        public final ListBuilder<E> clear() {
            this.getCollection().clear();
            return this;
        }

        public List<E> build() {
            return (List<E>) this.getCollection();
        }
    }

    public static final class SetBuilder<E> extends CollectionBuilder<E> {

        private SetBuilder(Set<E> set) {
            super(set);
        }

        public final SetBuilder<E> add(E element) {
            this.getCollection().add(element);
            return this;
        }

        public final SetBuilder<E> addAll(Collection<E> collection) {
            this.getCollection().addAll(collection);
            return this;
        }

        public final SetBuilder<E> remove(Object object) {
            this.getCollection().remove(object);
            return this;
        }

        public final SetBuilder<E> clear() {
            this.getCollection().clear();
            return this;
        }

        public Set<E> build() {
            return (Set<E>) this.getCollection();
        }
    }

    public static final <E> ListBuilder<E> newListBuilder(List<E> holder) {
        return new ListBuilder<E>(holder);
    }

    public static final <E> ListBuilder<E> newListBuilder() {
        return newListBuilder(new LinkedList<E>());
    }

    public static final <E> ListBuilder<E> newListBuilder(@NonNull Class<E> elementType) {
        return newListBuilder(new LinkedList<E>());
    }

    public static final <E> SetBuilder<E> newSetBuilder(Set<E> holder) {
        return new SetBuilder<E>(holder);
    }

    public static final <E> SetBuilder<E> newSetBuilder() {
        return newSetBuilder(new HashSet<E>());
    }

    public static final <E> SetBuilder<E> newSetBuilder(@NonNull Class<E> elementType) {
        return newSetBuilder(new HashSet<E>());
    }
}
