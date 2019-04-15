package com.chen.core.util.function;

public interface Consumer<T, E extends Throwable> {
    void accept(T t) throws E;
}