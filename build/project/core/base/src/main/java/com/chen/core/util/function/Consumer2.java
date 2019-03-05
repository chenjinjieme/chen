package com.chen.core.util.function;

public interface Consumer2<T, E extends Throwable, F extends Throwable> {
    void accept(T t) throws E, F;
}
