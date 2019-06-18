package io.gridgo.bean.impl;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.gridgo.bean.BElement;
import lombok.NonNull;

@SuppressWarnings("unchecked")
public class MutableBArray extends AbstractBArray {

    private final List<BElement> holder;

    public MutableBArray(@NonNull List<BElement> holder) {
        this.holder = holder;
    }

    public void forEach(Consumer<? super BElement> action) {
        holder.forEach(action);
    }

    public int size() {
        return holder.size();
    }

    public boolean isEmpty() {
        return holder.isEmpty();
    }

    public boolean contains(Object o) {
        return holder.contains(o);
    }

    public Iterator<BElement> iterator() {
        return holder.iterator();
    }

    public Object[] toArray() {
        return holder.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return holder.toArray(a);
    }

    public boolean add(@NonNull BElement e) {
        return holder.add(e);
    }

    public boolean remove(Object o) {
        return holder.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return holder.containsAll(c);
    }

    public boolean addAll(@NonNull Collection<? extends BElement> c) {
        return holder.addAll(c.stream().filter(Objects::nonNull).collect(Collectors.toList()));
    }

    public boolean addAll(int index, @NonNull Collection<? extends BElement> c) {
        return holder.addAll(index, c.stream().filter(Objects::nonNull).collect(Collectors.toList()));
    }

    public boolean removeAll(Collection<?> c) {
        return holder.removeAll(c);
    }

    public <T> T[] toArray(IntFunction<T[]> generator) {
        return holder.toArray(generator);
    }

    public boolean retainAll(Collection<?> c) {
        return holder.retainAll(c);
    }

    public void replaceAll(UnaryOperator<BElement> operator) {
        holder.replaceAll(operator);
    }

    public void sort(Comparator<? super BElement> c) {
        holder.sort(c);
    }

    public void clear() {
        holder.clear();
    }

    public BElement get(int index) {
        return holder.get(index);
    }

    public boolean removeIf(Predicate<? super BElement> filter) {
        return holder.removeIf(filter);
    }

    public BElement set(int index, @NonNull BElement element) {
        return holder.set(index, element);
    }

    public void add(int index, @NonNull BElement element) {
        holder.add(index, element);
    }

    public BElement remove(int index) {
        return holder.remove(index);
    }

    public int indexOf(Object o) {
        return holder.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return holder.lastIndexOf(o);
    }

    public ListIterator<BElement> listIterator() {
        return holder.listIterator();
    }

    public ListIterator<BElement> listIterator(int index) {
        return holder.listIterator(index);
    }

    public List<BElement> subList(int fromIndex, int toIndex) {
        return holder.subList(fromIndex, toIndex);
    }

    public Spliterator<BElement> spliterator() {
        return holder.spliterator();
    }

    public Stream<BElement> stream() {
        return holder.stream();
    }

    public Stream<BElement> parallelStream() {
        return holder.parallelStream();
    }

}