package com.shivam151990.patterns.iterator.book;

public interface Aggregator<T> {
    Iterator<T> getIterator();
}
