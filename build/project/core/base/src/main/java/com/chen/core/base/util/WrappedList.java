package com.chen.core.base.util;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class WrappedList<E> implements List<E> {
    private List<E> list;

    public WrappedList(List<E> list) {
        this.list = list;
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public boolean contains(Object o) {
        return list.contains(o);
    }

    public Iterator<E> iterator() {
        return list.iterator();
    }

    public void forEach(Consumer<? super E> action) {
        list.forEach(action);
    }

    public Object[] toArray() {
        return list.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    public <T> T[] toArray(IntFunction<T[]> generator) {
        return list.toArray(generator);
    }

    public boolean add(E e) {
        return list.add(e);
    }

    public boolean remove(Object o) {
        return list.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    public boolean addAll(Collection<? extends E> c) {
        return list.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        return list.addAll(index, c);
    }

    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    public boolean removeIf(Predicate<? super E> filter) {
        return list.removeIf(filter);
    }

    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    public void replaceAll(UnaryOperator<E> operator) {
        list.replaceAll(operator);
    }

    public void sort(Comparator<? super E> c) {
        list.sort(c);
    }

    public void clear() {
        list.clear();
    }

    public E get(int index) {
        return list.get(index);
    }

    public E set(int index, E element) {
        return list.set(index, element);
    }

    public void add(int index, E element) {
        list.add(index, element);
    }

    public E remove(int index) {
        return list.remove(index);
    }

    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    public ListIterator<E> listIterator() {
        return list.listIterator();
    }

    public ListIterator<E> listIterator(int index) {
        return list.listIterator(index);
    }

    public List<E> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    public Spliterator<E> spliterator() {
        return list.spliterator();
    }

    public Stream<E> stream() {
        return list.stream();
    }

    public Stream<E> parallelStream() {
        return list.parallelStream();
    }

    public int hashCode() {
        return list.hashCode();
    }

    public boolean equals(Object obj) {
        return obj == this || obj instanceof List && list.equals(obj);
    }

    public String toString() {
        return list.toString();
    }
}
